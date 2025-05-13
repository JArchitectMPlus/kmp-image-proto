import SwiftUI
import UIKit
import shared

struct CanvasView: UIViewRepresentable {
    @ObservedObject var canvasViewModel: CanvasViewModelWrapper
    @ObservedObject var layerViewModel: LayerViewModelWrapper
    
    func makeUIView(context: Context) -> CanvasUIView {
        let canvasView = CanvasUIView(
            canvasViewModel: canvasViewModel,
            layerViewModel: layerViewModel,
            delegate: context.coordinator
        )
        return canvasView
    }
    
    func updateUIView(_ uiView: CanvasUIView, context: Context) {
        // Update the view when the layers or canvas changes
        uiView.updateLayers()

        // Post notification to trigger redraw
        NotificationCenter.default.post(name: NSNotification.Name("LayersChanged"), object: nil)
    }
    
    func makeCoordinator() -> Coordinator {
        Coordinator(self)
    }
    
    class Coordinator: NSObject {
        var parent: CanvasView
        
        init(_ parent: CanvasView) {
            self.parent = parent
        }
        
        func layerSelected(_ layer: Layer?) {
            parent.layerViewModel.selectLayer(layer)
        }
        
        func layerMoved(_ layer: Layer, toX: Float, toY: Float) {
            // Create a position transformation for the layer movement
            let positionTransformation = PositionTransformation(x: toX, y: toY)
            
            // Update the layer position
            if let imageLayer = layer as? ImageLayer {
                imageLayer.x = toX
                imageLayer.y = toY
                
                // Apply transformations directly
                imageLayer.applyTransformations()
                
                parent.layerViewModel.updateLayer(layer: imageLayer)
                parent.canvasViewModel.updateLayer(layer: imageLayer)
            } else if let textLayer = layer as? TextLayer {
                textLayer.x = toX
                textLayer.y = toY
                
                // Apply transformations directly
                textLayer.applyTransformations()
                
                parent.layerViewModel.updateLayer(layer: textLayer)
                parent.canvasViewModel.updateLayer(layer: textLayer)
            }
        }
    }
}

// UIKit Canvas view for drawing layers
class CanvasUIView: UIView {
    private let canvasViewModel: CanvasViewModelWrapper
    private let layerViewModel: LayerViewModelWrapper
    private let delegate: CanvasView.Coordinator
    
    // Tracking touch for dragging
    private var touchStartLocation: CGPoint = .zero
    private var layerStartPosition: CGPoint = .zero
    private var isDragging = false
    private var currentlyDraggedLayer: Layer? = nil
    
    init(canvasViewModel: CanvasViewModelWrapper, layerViewModel: LayerViewModelWrapper, delegate: CanvasView.Coordinator) {
        self.canvasViewModel = canvasViewModel
        self.layerViewModel = layerViewModel
        self.delegate = delegate
        super.init(frame: .zero)
        self.backgroundColor = UIColor.lightGray
        self.isUserInteractionEnabled = true
        
        // Set up observers for layer changes
        NotificationCenter.default.addObserver(
            self,
            selector: #selector(layersChanged),
            name: NSNotification.Name("LayersChanged"),
            object: nil
        )
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    deinit {
        NotificationCenter.default.removeObserver(self)
    }
    
    @objc func layersChanged() {
        // Triggered when layers are modified externally
        setNeedsDisplay()
    }
    
    func updateLayers() {
        setNeedsDisplay()
    }
    
    // MARK: - Touch Handling
    
    override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
        guard let touch = touches.first else { return }
        let location = touch.location(in: self)
        
        // Check if we touched a layer
        if let layer = findLayerAt(location) {
            // Select the layer
            delegate.layerSelected(layer)
            
            // Start dragging
            isDragging = true
            currentlyDraggedLayer = layer
            touchStartLocation = location
            
            // Store the layer's starting position
            if let imageLayer = layer as? ImageLayer {
                layerStartPosition = CGPoint(x: CGFloat(imageLayer.x), y: CGFloat(imageLayer.y))
            } else if let textLayer = layer as? TextLayer {
                layerStartPosition = CGPoint(x: CGFloat(textLayer.x), y: CGFloat(textLayer.y))
            }
        } else {
            // Deselect current layer if tapping empty space
            delegate.layerSelected(nil)
            isDragging = false
            currentlyDraggedLayer = nil
        }
    }
    
    override func touchesMoved(_ touches: Set<UITouch>, with event: UIEvent?) {
        guard isDragging, let touch = touches.first, let layer = currentlyDraggedLayer else { return }
        
        let location = touch.location(in: self)
        let deltaX = location.x - touchStartLocation.x
        let deltaY = location.y - touchStartLocation.y
        
        // Calculate new position
        let newX = Float(layerStartPosition.x + deltaX)
        let newY = Float(layerStartPosition.y + deltaY)
        
        // Update the layer position through the delegate
        delegate.layerMoved(layer, toX: newX, toY: newY)
        
        // Force a redraw
        setNeedsDisplay()
    }
    
    override func touchesEnded(_ touches: Set<UITouch>, with event: UIEvent?) {
        isDragging = false
        currentlyDraggedLayer = nil
    }
    
    override func touchesCancelled(_ touches: Set<UITouch>, with event: UIEvent?) {
        isDragging = false
        currentlyDraggedLayer = nil
    }
    
    // MARK: - Layer Detection
    
    private func findLayerAt(_ point: CGPoint) -> Layer? {
        // Go through layers in reverse order (top to bottom)
        for layer in canvasViewModel.layers.reversed() {
            if !layer.isVisible() { continue }
            
            if let imageLayer = layer as? ImageLayer {
                // Get image dimensions
                var width: CGFloat = 200
                var height: CGFloat = 200
                
                if let image = imageLayer.image as? UIImage {
                    width = image.size.width
                    height = image.size.height
                }
                
                // Apply scale
                width *= CGFloat(imageLayer.scale)
                height *= CGFloat(imageLayer.scale)
                
                // Create a transformation matrix to handle rotation
                var transform = CGAffineTransform.identity
                transform = transform.translatedBy(x: CGFloat(imageLayer.x), y: CGFloat(imageLayer.y))
                transform = transform.rotated(by: CGFloat(imageLayer.rotation) * .pi / 180)
                
                // Create a path for the layer
                let layerPath = UIBezierPath(rect: CGRect(
                    x: -width/2,
                    y: -height/2,
                    width: width,
                    height: height
                ))
                
                // Apply the transform to the path
                layerPath.apply(transform)
                
                // Check if the point is inside the path
                if layerPath.contains(point) {
                    return imageLayer
                }
            } else if let textLayer = layer as? TextLayer {
                // Calculate text size
                let attributes: [NSAttributedString.Key: Any] = [
                    .font: UIFont.systemFont(ofSize: 20 * CGFloat(textLayer.scale))
                ]
                let text = textLayer.text as NSString
                let textSize = text.size(withAttributes: attributes)
                
                // Create a transformation matrix to handle rotation
                var transform = CGAffineTransform.identity
                transform = transform.translatedBy(x: CGFloat(textLayer.x), y: CGFloat(textLayer.y))
                transform = transform.rotated(by: CGFloat(textLayer.rotation) * .pi / 180)
                
                // Create a path for the text
                let textPath = UIBezierPath(rect: CGRect(
                    x: -textSize.width/2,
                    y: -textSize.height/2,
                    width: textSize.width,
                    height: textSize.height
                ))
                
                // Apply the transform to the path
                textPath.apply(transform)
                
                // Check if the point is inside the path
                if textPath.contains(point) {
                    return textLayer
                }
            }
        }
        
        return nil
    }
    
    // MARK: - Drawing
    
    override func draw(_ rect: CGRect) {
        super.draw(rect)
        
        guard let context = UIGraphicsGetCurrentContext() else { return }
        
        // Set the canvas background
        context.setFillColor(UIColor.white.cgColor)
        context.fill(rect)
        
        // Draw each layer
        for layer in canvasViewModel.layers {
            if !layer.isVisible() { continue }

            if let imageLayer = layer as? ImageLayer {
                drawImageLayer(imageLayer, in: context)
            } else if let textLayer = layer as? TextLayer {
                drawTextLayer(textLayer, in: context)
            }
        }
    }
    
    // Draw an image layer with transformations
    private func drawImageLayer(_ layer: ImageLayer, in context: CGContext) {
        guard let image = layer.image as? UIImage else { return }
        
        // Save context state
        context.saveGState()
        
        // Apply transformations
        context.translateBy(x: CGFloat(layer.x), y: CGFloat(layer.y))
        context.rotate(by: CGFloat(layer.rotation) * .pi / 180)
        context.scaleBy(x: CGFloat(layer.scale), y: CGFloat(layer.scale))
        
        // Draw the image centered at the origin
        let width = image.size.width
        let height = image.size.height
        image.draw(in: CGRect(x: -width/2, y: -height/2, width: width, height: height))
        
        // Draw selection outline if this layer is selected
        if layerViewModel.selectedLayer?.id == layer.id {
            context.setStrokeColor(UIColor.blue.cgColor)
            context.setLineWidth(2)
            context.stroke(CGRect(
                x: -width/2 - 2,
                y: -height/2 - 2,
                width: width + 4,
                height: height + 4
            ))
        }
        
        // Restore context state
        context.restoreGState()
    }
    
    // Draw a text layer with transformations
    private func drawTextLayer(_ layer: TextLayer, in context: CGContext) {
        // Save context state
        context.saveGState()
        
        // Apply transformations
        context.translateBy(x: CGFloat(layer.x), y: CGFloat(layer.y))
        context.rotate(by: CGFloat(layer.rotation) * .pi / 180)
        context.scaleBy(x: CGFloat(layer.scale), y: CGFloat(layer.scale))
        
        // Parse color
        var color = UIColor.black
        if let layerColor = UIColor.fromHexString(layer.color) {
            color = layerColor
        }
        
        // Set text attributes
        let attributes: [NSAttributedString.Key: Any] = [
            .font: UIFont.systemFont(ofSize: 20),
            .foregroundColor: color
        ]
        
        // Calculate text size
        let text = layer.text as NSString
        let textSize = text.size(withAttributes: attributes)
        
        // Draw the text
        text.draw(at: CGPoint(x: -textSize.width/2, y: -textSize.height/2), withAttributes: attributes)
        
        // Draw selection outline if this layer is selected
        if layerViewModel.selectedLayer?.id == layer.id {
            context.setStrokeColor(UIColor.blue.cgColor)
            context.setLineWidth(2)
            context.stroke(CGRect(
                x: -textSize.width/2 - 2,
                y: -textSize.height/2 - 2,
                width: textSize.width + 4,
                height: textSize.height + 4
            ))
        }
        
        // Restore context state
        context.restoreGState()
    }
}

// Helper extension UIColor method for hex string in Canvas View
private extension UIColor {
    // We're using a new method name to avoid conflict with the one in LayerControlsView
    static func fromHexString(_ hexString: String) -> UIColor? {
        var cleanHexString = hexString.trimmingCharacters(in: .whitespacesAndNewlines)
        cleanHexString = cleanHexString.replacingOccurrences(of: "#", with: "")

        var rgbValue: UInt64 = 0
        guard Scanner(string: cleanHexString).scanHexInt64(&rgbValue) else {
            return nil
        }

        return UIColor(
            red: CGFloat((rgbValue & 0xFF0000) >> 16) / 255.0,
            green: CGFloat((rgbValue & 0x00FF00) >> 8) / 255.0,
            blue: CGFloat(rgbValue & 0x0000FF) / 255.0,
            alpha: 1.0
        )
    }
}
