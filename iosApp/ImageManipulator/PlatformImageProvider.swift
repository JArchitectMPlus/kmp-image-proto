import shared
import Foundation

// Define the ImageProvider protocol to match the expected Kotlin interface
@objc public protocol ImageProvider {
    func loadImage(path: String, completionHandler: @escaping (Any?, Error?) -> Void)
    func saveImage(image: Any?, path: String, completionHandler: @escaping (Bool, Error?) -> Void)
    func getImageWidth(image: Any?) -> Int32
    func getImageHeight(image: Any?) -> Int32
}