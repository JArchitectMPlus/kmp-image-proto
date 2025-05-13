import Foundation

extension Float {
    // Implementation of floating point modulo operation
    func modulo(_ modulus: Float) -> Float {
        return truncatingRemainder(dividingBy: modulus)
    }
}
