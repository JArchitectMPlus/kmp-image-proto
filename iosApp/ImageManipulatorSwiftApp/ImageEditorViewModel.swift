import SwiftUI
import shared

class ImageEditorViewModel: ObservableObject {
    @Published var selectedImage: UIImage?
    
    // Shared Kotlin ViewModel for transformation calculations
    private let sharedViewModel = IOSImageViewModel()
    
    // MARK: - Image Loading
    
    func loadImage(_ image: UIImage) {
        selectedImage = image
        sharedViewModel.setImage(newImage: image)
    }
    
    // MARK: - Transformations
    
    func updateScale(scale: Float) {
        sharedViewModel.setScale(newScale: scale)
    }
    
    func updateRotation(degrees: Float) {
        sharedViewModel.setRotation(degrees: degrees)
    }
    
    func updateOffset(x: Float, y: Float) {
        sharedViewModel.applyTranslation(dx: x, dy: y)
    }
    
    func resetAll() {
        sharedViewModel.resetTransformations()
    }
}