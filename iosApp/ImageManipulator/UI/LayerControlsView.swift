import SwiftUI
import shared
import UIKit

struct LayerControlsView: View {
    @ObservedObject var layerViewModel: LayerViewModelWrapper
    @ObservedObject var canvasViewModel: CanvasViewModelWrapper
    @StateObject private var transformationSystem = TransformationSystemWrapper()
    
    @State private var showingImagePicker = false
    @State private var selectedUIImage: UIImage? = nil
    @State private var showingExportOptions = false
 @State private var showingColorPicker = false
    @State private var exportedBase64: String = ""
    
    private let imageProvider = IOSImageProvider()
    
    var body: some View {
        VStack(spacing: 16) {
            Text("Image Manipulator")
                .font(.headline)
            if let layer = layerViewModel.selectedLayer {
                VStack(spacing: 8) {
                    HStack {
                        Text("Scale: \(scaleValue(for: layer))")
                        Spacer()
                        
                        // Decrease scale button
                        Button(action: {
                            decreaseScale(for: layer)
                        }) {
                            Image(systemName: "minus")
                                .padding(8)
                        }
                        
                        // Increase scale button
                        Button(action: {
                            increaseScale(for: layer)
                        }) {
                            Image(systemName: "plus")
                                .padding(8)
                        }
                        
                        // Reset scale button
                        Button(action: {
                            resetScale(for: layer)
                        }) {
                            Image(systemName: "arrow.counterclockwise")
                                .padding(8)
                        }
                    }
                    
                    // Rotation controls
                    HStack {
                        Text("Rotation: \(rotationValue(for: layer))")
                        Spacer()
                        
                        // Rotate counter-clockwise button
                        Button(action: {
                            rotateCounterClockwise(for: layer)
                        }) {
                            Image(systemName: "rotate.left")
                                .padding(8)
                        }
                        
                        // Rotate clockwise button
                        Button(action: {
                            rotateClockwise(for: layer)
                        }) {
                            Image(systemName: "rotate.right")
                                .padding(8)
                        }
                        
                        // Reset rotation button
                        Button(action: {
                            resetRotation(for: layer)
                        }) {
                            Image(systemName: "arrow.counterclockwise")
                                .padding(8)
                        }
                    }
                    
                    if let textLayer = layer as? TextLayer {
                        VStack(spacing: 8) {
                            Divider()
                            Text("Text Properties")
                                .font(.subheadline)
                                .fontWeight(.bold)
                            
                            // Text content
                            TextField("Text Content", text: Binding(
                                get: { textLayer.text },
                                set: { updateText(textLayer, newText: $0) }
                            ))
                            .textFieldStyle(RoundedBorderTextFieldStyle())
                            .padding(.vertical, 4)
                            
                            // Text color picker
                            HStack {
                                Text("Color:")
                                Spacer()
                                ColorPicker("", selection: Binding(
                                    get: { UIColor(hexString: textLayer.color) ?? UIColor.black },
                                    set: { newColor in
                                        updateTextColor(textLayer, newColor: newColor.toHexString())
                                    }
                                ))
                                .labelsHidden() // Hide the default label
                                .fixedSize()

                                .frame(width: 100)
                            }
                        }
                    }
                    
                    // Visibility toggle
                    HStack {
                        Text("Visible")
                        Spacer()
                        Toggle("", isOn: Binding(
                            get: { layer.isVisible() },
                            set: { toggleVisibility(layer, isVisible: $0) }
                        ))
                    }
                    
                    // Delete layer button
                    Button(action: {
                        deleteLayer(layer)
                    }) {
                        Text("Delete Layer")
                            .foregroundColor(.white)
                            .padding()
                            .frame(maxWidth: .infinity)
                            .background(Color.red)
                            .cornerRadius(8)
                    }
                }
                .padding(.horizontal)
            }
            
            // Add text layer button
            Divider()
            
            // Add text layer button
            Button(action: {
                addTextLayer()
            }) {
                HStack {
                    Image(systemName: "textformat")
                    Text("Add Text Layer")
                }
                .frame(minWidth: 0, maxWidth: .infinity)
                .padding()
                .background(Color.blue)
                .foregroundColor(.white)
                .cornerRadius(8)
            }
            .padding(.horizontal)
            
            // Add image button

            Button(action: {
                showingImagePicker = true
            }) {
                HStack {
                    Image(systemName: "photo")
                    Text("Add Image")
                }
                .frame(minWidth: 0, maxWidth: .infinity)
                .padding()
                .background(Color.blue)
                .foregroundColor(.white)
                .cornerRadius(8)
            }
            .padding(.horizontal)
            .sheet(isPresented: $showingImagePicker) {
                ImagePicker(selectedImage: $selectedUIImage)
                    .onDisappear {
                        if let image = selectedUIImage {
                            addImageLayer(image)
                        }
                    }
            }
            
            // Export button (Base64)

            Button(action: {
                exportToBase64()
            }) {
                HStack {
                    Image(systemName: "square.and.arrow.up")
                    Text("Export to Base64")
                }
                .frame(minWidth: 0, maxWidth: .infinity)
                .padding()
                .background(Color.blue)
                .foregroundColor(.white)
                .cornerRadius(8)
            }
            .padding(.horizontal)
            .alert(isPresented: $showingExportOptions) {
                Alert(
                    title: Text("Canvas Exported"),
                    message: Text("Canvas exported to clipboard as Base64"),
                    dismissButton: .default(Text("OK"))
                )
            }
            
            // Clear canvas button

            Button(action: {
                clearCanvas()
            }) {
                HStack {
                    Image(systemName: "trash")
                    Text("Clear Canvas")
                }
                .frame(minWidth: 0, maxWidth: .infinity)
                .padding()
                .background(Color.red)
                .foregroundColor(.white)
                .cornerRadius(8)
            }
            .padding(.horizontal)
            
            Divider()
            Text("Layers (\(layerViewModel.layers.count))")
                .font(.headline)
                .padding(.top)
            
            ScrollView {
                VStack(spacing: 8) {
                    ForEach(layerViewModel.layers, id: \.id) { layer in
                        LayerItemView(
                            layer: layer,
                            isSelected: layer.id == layerViewModel.selectedLayer?.id,
                            onSelect: {
                                layerViewModel.selectLayer(layer)
                            }
                        )
                    }
                }
                .padding(.horizontal)
            }
        }
        .padding(.bottom)
    }
    
    // MARK: - Transformation Helper Functions
    
    private func scaleValue(for layer: Layer) -> String {
        let scale: Float
        if let imageLayer = layer as? ImageLayer {
            scale = imageLayer.scale
        } else if let textLayer = layer as? TextLayer {
            scale = textLayer.scale
        } else {
            scale = 1.0
        }
        return String(format: "%.1f", scale)
    }
    
    private func rotationValue(for layer: Layer) -> String {
        let rotation: Float
        if let imageLayer = layer as? ImageLayer {
            rotation = imageLayer.rotation
        } else if let textLayer = layer as? TextLayer {
            rotation = textLayer.rotation
        } else {
            rotation = 0.0
        }
        return "\(Int(rotation))°"
    }
    
    private func decreaseScale(for layer: Layer) {
        if let imageLayer = layer as? ImageLayer {
            // Calculate new scale and ensure it's not too small
            let newScale = max(0.5, imageLayer.scale - 0.1)
            
            // Create a scale transformation and apply it
            let scaleTransformation = ScaleTransformation(scale: newScale)
            transformationSystem.addTransformation(transformation: scaleTransformation)
            
            // Set scale directly on the layer
            imageLayer.scale = newScale
            
            // Update the layer in both view models
            layerViewModel.updateLayer(layer: imageLayer)
            canvasViewModel.updateLayer(layer: imageLayer)
        } else if let textLayer = layer as? TextLayer {
            // Same process for text layers
            let newScale = max(0.5, textLayer.scale - 0.1)
            
            let scaleTransformation = ScaleTransformation(scale: newScale)
            transformationSystem.addTransformation(transformation: scaleTransformation)
            
            textLayer.scale = newScale
            
            layerViewModel.updateLayer(layer: textLayer)
            canvasViewModel.updateLayer(layer: textLayer)
        }
    }
    
    private func increaseScale(for layer: Layer) {
        if let imageLayer = layer as? ImageLayer {
            // Calculate new scale and ensure it's not too large
            let newScale = min(5.0, imageLayer.scale + 0.1)
            
            // Create a scale transformation and apply it
            let scaleTransformation = ScaleTransformation(scale: newScale)
            transformationSystem.addTransformation(transformation: scaleTransformation)
            
            // Set scale directly on the layer
            imageLayer.scale = newScale
            
            // Update the layer in both view models
            layerViewModel.updateLayer(layer: imageLayer)
            canvasViewModel.updateLayer(layer: imageLayer)
        } else if let textLayer = layer as? TextLayer {
            // Same process for text layers
            let newScale = min(5.0, textLayer.scale + 0.1)
            
            let scaleTransformation = ScaleTransformation(scale: newScale)
            transformationSystem.addTransformation(transformation: scaleTransformation)
            
            textLayer.scale = newScale
            
            layerViewModel.updateLayer(layer: textLayer)
            canvasViewModel.updateLayer(layer: textLayer)
        }
    }
    
    private func resetScale(for layer: Layer) {
        if let imageLayer = layer as? ImageLayer {
            // Create a scale transformation for reset
            let scaleTransformation = ScaleTransformation(scale: 1.0)
            transformationSystem.addTransformation(transformation: scaleTransformation)
            
            // Set scale directly to 1.0
            imageLayer.scale = 1.0
            
            // Update the layer in both view models
            layerViewModel.updateLayer(layer: imageLayer)
            canvasViewModel.updateLayer(layer: imageLayer)
        } else if let textLayer = layer as? TextLayer {
            // Same process for text layers
            let scaleTransformation = ScaleTransformation(scale: 1.0)
            transformationSystem.addTransformation(transformation: scaleTransformation)
            
            textLayer.scale = 1.0
            
            layerViewModel.updateLayer(layer: textLayer)
            canvasViewModel.updateLayer(layer: textLayer)
        }
    }
    
    private func rotateCounterClockwise(for layer: Layer) {
        if let imageLayer = layer as? ImageLayer {
            // Calculate new rotation angle (counter-clockwise)
            let newRotation = (imageLayer.rotation - 15) % 360
            
            // Create a rotation transformation and apply it
            let rotationTransformation = RotationTransformation(degrees: newRotation)
            transformationSystem.addTransformation(transformation: rotationTransformation)
            
            // Set rotation directly on the layer
            imageLayer.rotation = newRotation
            
            // Update the layer in both view models
            layerViewModel.updateLayer(layer: imageLayer)
            canvasViewModel.updateLayer(layer: imageLayer)
        } else if let textLayer = layer as? TextLayer {
            // Same process for text layers
            let newRotation = (textLayer.rotation - 15) % 360
            
            let rotationTransformation = RotationTransformation(degrees: newRotation)
            transformationSystem.addTransformation(transformation: rotationTransformation)
            
            textLayer.rotation = newRotation
            
            layerViewModel.updateLayer(layer: textLayer)
            canvasViewModel.updateLayer(layer: textLayer)
        }
    }
    
    private func rotateClockwise(for layer: Layer) {
        if let imageLayer = layer as? ImageLayer {
            // Calculate new rotation angle (clockwise)
            let newRotation = (imageLayer.rotation + 15) % 360
            
            // Create a rotation transformation and apply it
            let rotationTransformation = RotationTransformation(degrees: newRotation)
            transformationSystem.addTransformation(transformation: rotationTransformation)
            
            // Set rotation directly on the layer
            imageLayer.rotation = newRotation
            
            // Update the layer in both view models
            layerViewModel.updateLayer(layer: imageLayer)
            canvasViewModel.updateLayer(layer: imageLayer)
        } else if let textLayer = layer as? TextLayer {
            // Same process for text layers
            let newRotation = (textLayer.rotation + 15) % 360
            
            let rotationTransformation = RotationTransformation(degrees: newRotation)
            transformationSystem.addTransformation(transformation: rotationTransformation)
            
            textLayer.rotation = newRotation
            
            layerViewModel.updateLayer(layer: textLayer)
            canvasViewModel.updateLayer(layer: textLayer)
        }
    }
    
    private func resetRotation(for layer: Layer) {
        if let imageLayer = layer as? ImageLayer {
            // Create a rotation transformation for reset
            let rotationTransformation = RotationTransformation(degrees: 0)
            transformationSystem.addTransformation(transformation: rotationTransformation)
            
            // Set rotation directly to 0
            imageLayer.rotation = 0
            
            // Update the layer in both view models
            layerViewModel.updateLayer(layer: imageLayer)
            canvasViewModel.updateLayer(layer: imageLayer)
        } else if let textLayer = layer as? TextLayer {
            // Same process for text layers
            let rotationTransformation = RotationTransformation(degrees: 0)
            transformationSystem.addTransformation(transformation: rotationTransformation)
            
            textLayer.rotation = 0
            
            layerViewModel.updateLayer(layer: textLayer)
            canvasViewModel.updateLayer(layer: textLayer)
        }
    }
    
    // MARK: - Text Layer Operations
    
    private func updateText(_ textLayer: TextLayer, newText: String) {
        textLayer.text = newText
        layerViewModel.updateLayer(layer: textLayer)
        canvasViewModel.updateLayer(layer: textLayer)
    }
    
    private func updateTextColor(_ textLayer: TextLayer, newColor: String?) {
        if let color = newColor {
            textLayer.color = color
            layerViewModel.updateLayer(layer: textLayer)
            canvasViewModel.updateLayer(layer: textLayer)
        } else {
            // Handle invalid color string or do nothing
        }

        canvasViewModel.updateLayer(layer: textLayer)
    }
    
    // MARK: - Layer Operations
    
    private func toggleVisibility(_ layer: Layer, isVisible: Bool) {
        if let imageLayer = layer as? ImageLayer {
            imageLayer.setVisibility(isVisible: isVisible)
            layerViewModel.updateLayer(layer: imageLayer)
            canvasViewModel.updateLayer(layer: imageLayer)
        } else if let textLayer = layer as? TextLayer {
            textLayer.setVisibility(isVisible: isVisible)
            layerViewModel.updateLayer(layer: textLayer)
            canvasViewModel.updateLayer(layer: textLayer)
        }
    }
    
    private func deleteLayer(_ layer: Layer) {
        layerViewModel.removeLayer(layer: layer)
        canvasViewModel.removeLayer(layerId: layer.id)
    }
    
    private func addTextLayer() {
        // Create a new transformation system for the layer
        let ts = TransformationSystem()
        
        let layerId = UUID().uuidString
        let textLayer = TextLayer(
            text: "New Text",
            id: layerId,
            color: "#000000",
            font: "Default",
            curve: 0,
            x: 150,
            y: 150,
            scale: 1.0,
            rotation: 0,
            isVisible: true,
            transformationSystem: ts
        )
        
        canvasViewModel.addLayer(layer: textLayer)
        layerViewModel.addLayer(layer: textLayer)
        layerViewModel.selectLayer(textLayer)
    }
    
    private func addImageLayer(_ image: UIImage) {
        // Create a new transformation system for the layer
        let ts = TransformationSystem()
        
        // Create a unique ID for the layer
        let layerId = UUID().uuidString
        let path = "temp://\(layerId)"
        
        let layer = ImageLayer(
            path: path,
            id: layerId,
            image: image,
            scale: 1.0,
            x: 100,
            y: 100,
            rotation: 0,
            isVisible: true,
            transformationSystem: ts
        )
        
        canvasViewModel.addLayer(layer: layer)
        layerViewModel.addLayer(layer: layer)
        layerViewModel.selectLayer(layer)
    }
    
    // MARK: - Canvas Operations
    
    private func exportToBase64() {
        // Create iOS equivalent of the AndroidCanvasExporter
        let exporter = SwiftCanvasExporter()
        
        // Get canvas and layers from view models
        let canvas = canvasViewModel.canvas
        let layers = layerViewModel.layers
        
        // Export to base64
        if let base64 = exporter.exportToBase64(canvas: canvas, layers: layers) {
            // Copy to clipboard
            UIPasteboard.general.string = base64
            exportedBase64 = base64
            showingExportOptions = true
        }
    }
    
    private func clearCanvas() {
        canvasViewModel.clear()
        layerViewModel.clear()
    }
}

// MARK: - Layer Item View

struct LayerItemView: View {
    let layer: Layer
    let isSelected: Bool
    let onSelect: () -> Void
    
    var body: some View {
        HStack {
            // Layer name/type
            Text(layerName)
                .fontWeight(isSelected ? .bold : .regular)
            
            Spacer()
            
            // Select button
            Button("Select") {
                onSelect()
            }
            .padding(.horizontal, 8)
            .padding(.vertical, 4)
            .background(Color.blue)
            .foregroundColor(.white)
            .cornerRadius(4)
        }
        .padding(8)
        .background(isSelected ? Color.blue.opacity(0.2) : Color.clear)
        .cornerRadius(4)
    }
    
    private var layerName: String {
        if let imageLayer = layer as? ImageLayer {
            return "Image Layer \(layer.id.prefix(4))"
        } else if let textLayer = layer as? TextLayer {
            let displayText = textLayer.text.count > 10 
                ? String(textLayer.text.prefix(10)) + "..."
                : textLayer.text
            return "Text: \(displayText)"
        } else {
            return "Layer \(layer.id.prefix(4))"
        }
    }
}

// MARK: - Helpers

// Wrapper for TransformationSystem to use in SwiftUI
class TransformationSystemWrapper: ObservableObject {
    private let transformationSystem = TransformationSystem()
    
    func addTransformation(transformation: TransformationManager) {
        transformationSystem.addTransformation(transformation: transformation)
    }
    
    func clearTransformations() {
        transformationSystem.clearTransformations()
    }
}

// Helper extension for UIColor to hex string
extension UIColor {
    func toHexString() -> String {
        var r: CGFloat = 0
        var g: CGFloat = 0
        var b: CGFloat = 0
        var a: CGFloat = 0

        getRed(&r, green: &g, blue: &b, alpha: &a)

        let rgb: Int = (Int)(r * 255) << 16 | (Int)(g * 255) << 8 | (Int)(b * 255) << 0
        return String(format: "#%06x", rgb)
    }

    convenience init?(hexString: String) {
        var cleanHexString = hexString.trimmingCharacters(in: .whitespacesAndNewlines)
        cleanHexString = cleanHexString.replacingOccurrences(of: "#", with: "")

        var rgbValue: UInt64 = 0
        Scanner(string: cleanHexString).scanHexInt64(&rgbValue)

        let red = CGFloat((rgbValue & 0xFF0000) >> 16) / 255.0
        let green = CGFloat((rgbValue & 0x00FF00) >> 8) / 255.0
        let blue = CGFloat(rgbValue & 0x0000FF) / 255.0

        self.init(red: red, green: green, blue: blue, alpha: 1.0)
    }
}



