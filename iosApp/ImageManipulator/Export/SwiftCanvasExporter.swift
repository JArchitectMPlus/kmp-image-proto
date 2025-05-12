swift
import UIKit
import Foundation
import shared

// Helper extension for UIColor from hex string
extension UIColor {
    convenience init?(hexString: String) {
        var cleanHexString = hexString.trimmingCharacters(in: .whitespacesAndNewlines)
        cleanHexString = cleanHexString.replacingOccurrences(of: "#", with: "")

        guard cleanHexString.count == 6 else { return nil }

        var rgbValue: UInt64 = 0
        Scanner(string: cleanHexString).scanHexInt64(&rgbValue)

        let red = CGFloat((rgbValue & 0xFF0000) >> 16) / 255.0
        let green = CGFloat((rgbValue & 0x00FF00) >> 8) / 255.0
        let blue = CGFloat(rgbValue & 0x0000FF) / 255.0

        self.init(red: red, green: green, blue: blue, alpha: 1.0)
    }
}

// Helper extension to convert Kotlin ByteArray to Swift Data (assuming `actualArray` is available)
extension ByteArray {
    var actualArray: [UInt8] {
        return [UInt8](unsafeUninitializedCapacity: Int(size)) { buffer, count in
            copyInto(buffer)
            count = Int(size)
        }
    }
}

// Helper extension to convert Swift Data to Kotlin ByteArray
extension Data {
    func toKByteArray() -> ByteArray {
        let array = [UInt8](self)
        let kByteArray = ByteArray(size: Int32(array.count))
        for (index, byte) in array.enumerated() {
            kByteArray[Int32(index)] = byte.toByte()
        }
        return kByteArray
    }
}


class SwiftCanvasExporter: CanvasExporter {

    override func exportToBase64(canvas: shared.Canvas, layers: [shared.Layer]): String {
        // Define canvas size (assuming a default size or getting from canvas model if available)
        let size = CGSize(width: 1000, height: 1000) // Example size, adjust as needed

        // Use UIGraphicsImageRenderer for drawing
        let renderer = UIGraphicsImageRenderer(size: size)

        let image = renderer.image { context in
            // Get the Core Graphics context
            let cgContext = context.cgContext

            // Fill with white background
            cgContext.setFillColor(UIColor.white.cgColor)
            cgContext.fill(CGRect(origin: .zero, size: size))

            // Sort layers to draw in correct order (assuming layers list is ordered)
             let sortedLayers = layers.sorted(by: { $0.id < $1.id }) // Example sorting

            // Draw each visible layer
            for layer in sortedLayers {
                if layer.isVisible() {
                    cgContext.saveGState() // Save context state

                    // Apply transformations (translate, rotate, scale)
                    cgContext.translateBy(x: CGFloat(layer.x), y: CGFloat(layer.y))
                    // Convert degrees to radians for rotation
                    cgContext.rotate(by: CGFloat(layer.rotation * Double.pi / 180.0))
                    cgContext.scaleBy(x: CGFloat(layer.scale), y: CGFloat(layer.scale))

                    // Draw layer content
                    if let imageLayer = layer as? shared.ImageLayer {
                        if let uiImage = imageLayer.image as? UIImage {
                            // Draw the image at the origin (0,0) since we've already translated and scaled
                            uiImage.draw(at: .zero)
                        }
                    } else if let textLayer = layer as? shared.TextLayer {
                        let text = textLayer.text
                        let color = UIColor(hexString: textLayer.color) ?? UIColor.black
                        // Use a reasonable default font and size, or get from layer if available
                        let font = UIFont.systemFont(ofSize: 50) // Example font size

                        let attributes: [NSAttributedString.Key: Any] = [
                            .font: font,
                            .foregroundColor: color
                        ]
                        let attributedString = NSAttributedString(string: text, attributes: attributes)

                         // Calculate text size to draw at correct position relative to the center
                        let textSize = attributedString.size()
                        let textDrawPoint = CGPoint(x: -textSize.width / 2, y: -textSize.height / 2)


                        attributedString.draw(at: textDrawPoint)
                    }

                    cgContext.restoreGState() // Restore context state
                }
            }
        }

        // Convert image to PNG data and then to Base64
        if let imageData = image.pngData() {
            return imageData.base64EncodedString()
        } else {
            return ""
        }
    }

    override func convertBytesToBase64(bytes: ByteArray): String {
        let data = Data(bytes: bytes.actualArray, count: bytes.actualArray.count)
        return data.base64EncodedString()
    }

    override fun convertBase64ToBytes(base64: String): ByteArray {
        if let data = Data(base64Encoded: base64) {
            return data.toKByteArray()
        } else {
            return ByteArray(0)
        }
    }
}