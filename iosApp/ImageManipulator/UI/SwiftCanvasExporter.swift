import UIKit
import shared

/// Swift implementation of the canvas exporter
class SwiftCanvasExporter {
    func exportToBase64(canvas: Canvas, layers: [Layer]) -> String? {
        // Create a graphics context with the canvas dimensions
        let width = CGFloat(canvas.width)
        let height = CGFloat(canvas.height)
        
        UIGraphicsBeginImageContextWithOptions(CGSize(width: width, height: height), false, 1.0)
        defer { UIGraphicsEndImageContext() }
        
        guard let context = UIGraphicsGetCurrentContext() else {
            return nil
        }
        
        // Fill with white background
        context.setFillColor(UIColor.white.cgColor)
        context.fill(CGRect(x: 0, y: 0, width: width, height: height))
        
        // Draw each layer
        for layer in layers {
            if !layer.isVisible() { continue }
            
            if let imageLayer = layer as? ImageLayer {
                drawImageLayer(imageLayer, in: context)
            } else if let textLayer = layer as? TextLayer {
                drawTextLayer(textLayer, in: context)
            }
        }
        
        // Get the drawn image
        guard let image = UIGraphicsGetImageFromCurrentImageContext() else {
            return nil
        }
        
        // Convert to base64
        guard let data = image.pngData() else {
            return nil
        }
        
        return data.base64EncodedString()
    }
    
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
        
        // Restore context state
        context.restoreGState()
    }
    
    private func drawTextLayer(_ layer: TextLayer, in context: CGContext) {
        // Save context state
        context.saveGState()
        
        // Apply transformations
        context.translateBy(x: CGFloat(layer.x), y: CGFloat(layer.y))
        context.rotate(by: CGFloat(layer.rotation) * .pi / 180)
        context.scaleBy(x: CGFloat(layer.scale), y: CGFloat(layer.scale))
        
        // Parse color
        var color = UIColor.black
        if let layerColor = try? UIColor(hexString: layer.color) {
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
        
        // Restore context state
        context.restoreGState()
    }
}

// Helper extension for UIColor from hex string
extension UIColor {
    convenience init(hexString: String) throws {
        var hexSanitized = hexString.trimmingCharacters(in: .whitespacesAndNewlines)
        hexSanitized = hexSanitized.replacingOccurrences(of: "#", with: "")
        
        var rgb: UInt64 = 0
        
        guard Scanner(string: hexSanitized).scanHexInt64(&rgb) else {
            throw NSError(domain: "UIColor+Hex", code: 1, userInfo: [NSLocalizedDescriptionKey: "Invalid hex color format"])
        }
        
        self.init(
            red: CGFloat((rgb & 0xFF0000) >> 16) / 255.0,
            green: CGFloat((rgb & 0x00FF00) >> 8) / 255.0,
            blue: CGFloat(rgb & 0x0000FF) / 255.0,
            alpha: 1.0
        )
    }
}