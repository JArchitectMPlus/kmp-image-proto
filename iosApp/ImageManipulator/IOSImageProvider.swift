import shared
import Foundation
import UIKit
import shared

/// iOS-specific implementation of image loading and saving
class IOSImageProvider: NSObject, ImageProvider {
    
    func loadImage(path: String, completionHandler: @escaping (Any?, Error?) -> Void) {
        // Handle different path formats
        if let url = URL(string: path) {
            // Try to load from URL
            loadImageFromURL(url) { image, error in
                completionHandler(image, error)
            }
        } else {
            // Try as a file path
            let fileURL = URL(fileURLWithPath: path)
            loadImageFromURL(fileURL) { image, error in
                completionHandler(image, error)
            }
        }
    }
    
    private func loadImageFromURL(_ url: URL, completionHandler: @escaping (UIImage?, Error?) -> Void) {
        // For remote URLs, use URLSession
        if url.scheme == "http" || url.scheme == "https" {
            let task = URLSession.shared.dataTask(with: url) { data, response, error in
                if let error = error {
                    completionHandler(nil, error)
                    return
                }
                
                guard let data = data else {
                    completionHandler(nil, NSError(domain: "ImageProvider", code: 1, userInfo: [NSLocalizedDescriptionKey: "No data received"]))
                    return
                }
                
                if let image = UIImage(data: data) {
                    completionHandler(image, nil)
                } else {
                    completionHandler(nil, NSError(domain: "ImageProvider", code: 2, userInfo: [NSLocalizedDescriptionKey: "Failed to create image from data"]))
                }
            }
            task.resume()
        } else {
            // Local file
            if let image = UIImage(contentsOfFile: url.path) {
                completionHandler(image, nil)
            } else {
                // Try loading from assets
                if let image = UIImage(named: url.lastPathComponent) {
                    completionHandler(image, nil)
                } else {
                    completionHandler(nil, NSError(domain: "ImageProvider", code: 3, userInfo: [NSLocalizedDescriptionKey: "File not found: \(url.path)"]))
                }
            }
        }
    }
    
    func saveImage(image: Any?, path: String, completionHandler: @escaping (Bool, Error?) -> Void) {
        guard let uiImage = image as? UIImage else {
            completionHandler(false, NSError(domain: "ImageProvider", code: 4, userInfo: [NSLocalizedDescriptionKey: "Not a valid UIImage"]))
            return
        }
        
        let url = URL(fileURLWithPath: path)
        
        // Create directory if needed
        let directory = url.deletingLastPathComponent()
        do {
            try FileManager.default.createDirectory(at: directory, withIntermediateDirectories: true)
        } catch {
            completionHandler(false, error)
            return
        }
        
        // Determine image format based on path extension
        if let data = getImageData(uiImage, for: url.pathExtension) {
            do {
                try data.write(to: url)
                completionHandler(true, nil)
            } catch {
                completionHandler(false, error)
            }
        } else {
            completionHandler(false, NSError(domain: "ImageProvider", code: 5, userInfo: [NSLocalizedDescriptionKey: "Failed to encode image"]))
        }
    }
    
    private func getImageData(_ image: UIImage, for extension: String) -> Data? {
        switch `extension`.lowercased() {
        case "jpg", "jpeg":
            return image.jpegData(compressionQuality: 0.8)
        case "png":
            return image.pngData()
        default:
            // Default to PNG
            return image.pngData()
        }
    }
    
    func getImageWidth(image: Any?) -> Int32 {
        guard let uiImage = image as? UIImage else { return 0 }
        return Int32(uiImage.size.width)
    }
    
    func getImageHeight(image: Any?) -> Int32 {
        guard let uiImage = image as? UIImage else { return 0 }
        return Int32(uiImage.size.height)
    }
}