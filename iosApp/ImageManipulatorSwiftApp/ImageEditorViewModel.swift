import SwiftUI
import shared

class ImageEditorViewModel: ObservableObject {
    @Published var selectedImage: UIImage?

    // Shared Kotlin ViewModel for transformation calculations
    private let sharedViewModel = ImageViewModel()
    
    // MARK: - Image Loading
    
    func loadImage(_ image: UIImage) {
        selectedImage = image
        // Create and add an image layer using the selected image
        let layerId = UUID().uuidString
        let layer = ImageLayer(
            path: "memory://\(layerId)",
            id: layerId,
            image: image,
            scale: 1.0,
            x: 100,
            y: 100,
            rotation: 0,
            isVisible: true,
            transformationSystem: nil
        )
        sharedViewModel.addLayer(layer: layer)
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