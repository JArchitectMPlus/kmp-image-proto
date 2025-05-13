import Foundation
import UIKit

// Common helper functions used across the app
func normalizeAngle(_ angle: Float) -> Float {
    var normalized = angle.truncatingRemainder(dividingBy: 360.0)
    if normalized < 0 {
        normalized += 360.0
    }
    return normalized
}

func colorToHexString(color: UIColor) -> String {
    var r: CGFloat = 0
    var g: CGFloat = 0
    var b: CGFloat = 0
    var a: CGFloat = 0
    
    color.getRed(&r, green: &g, blue: &b, alpha: &a)
    
    let rgb: Int = (Int)(r * 255) << 16 | (Int)(g * 255) << 8 | (Int)(b * 255) << 0
    
    return String(format: "#%06x", rgb)
}

func hexStringToColor(hexString: String) -> UIColor {
    var colorString = hexString.trimmingCharacters(in: .whitespacesAndNewlines)
    colorString = colorString.replacingOccurrences(of: "#", with: "").uppercased()
    
    let alpha: CGFloat = 1.0
    let red: CGFloat = colorComponentFrom(colorString: colorString, start: 0, length: 2)
    let green: CGFloat = colorComponentFrom(colorString: colorString, start: 2, length: 2)
    let blue: CGFloat = colorComponentFrom(colorString: colorString, start: 4, length: 2)
    
    return UIColor(red: red, green: green, blue: blue, alpha: alpha)
}

private func colorComponentFrom(colorString: String, start: Int, length: Int) -> CGFloat {
    let startIndex = colorString.index(colorString.startIndex, offsetBy: start)
    let endIndex = colorString.index(startIndex, offsetBy: length)
    let substring = colorString[startIndex..<endIndex]
    let fullHex = length == 2 ? substring : "\(substring)\(substring)"
    
    var hexComponent: UInt64 = 0
    Scanner(string: String(fullHex)).scanHexInt64(&hexComponent)
    
    return CGFloat(hexComponent) / 255.0
}