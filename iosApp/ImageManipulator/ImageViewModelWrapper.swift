import Foundation
import shared
import SwiftUI
import Combine

/// A wrapper class that adapts the Kotlin `ImageViewModel` to Swift's `ObservableObject` pattern
/// This allows us to use Kotlin view models with SwiftUI
class ImageViewModelWrapper: ObservableObject {
    // The underlying Kotlin view model
    private let viewModel: ImageViewModel
    
    // Published properties for SwiftUI to observe
    @Published var scale: Float = 1.0
    @Published var rotation: Float = 0.0
    @Published var layers: [Layer] = []
    @Published var selectedLayerId: String? = nil
    
    // Cancellables for Kotlin flow collection
    private var cancellables = Set<AnyCancellable>()
    
    init(viewModel: ImageViewModel = ImageViewModel()) {
        self.viewModel = viewModel
        setupSubscriptions()
    }
    
    /// Set up observers for Kotlin StateFlow objects
    private func setupSubscriptions() {
        // Observe scale changes
        viewModel.getScale().watch { [weak self] scale in
            guard let scale = scale as? Float else { return }
            DispatchQueue.main.async {
                self?.scale = scale
            }
        }
        
        // Observe rotation changes
        viewModel.getRotation().watch { [weak self] rotation in
            guard let rotation = rotation as? Float else { return }
            DispatchQueue.main.async {
                self?.rotation = rotation
            }
        }
        
        // Observe layer changes
        viewModel.getLayers().watch { [weak self] layers in
            guard let layers = layers as? [Layer] else { return }
            DispatchQueue.main.async {
                self?.layers = layers
            }
        }
        
        // Observe selected layer ID changes
        viewModel.getSelectedLayerId().watch { [weak self] selectedId in
            DispatchQueue.main.async {
                self?.selectedLayerId = selectedId as? String
            }
        }
    }
    
    // Forward actions to the Kotlin view model
    
    func resetTransformations() {
        viewModel.resetTransformations()
    }
    
    func applyScale(scaleFactor: Float) {
        viewModel.applyScale(scaleFactor: scaleFactor)
    }
    
    func setScale(newScale: Float) {
        viewModel.setScale(newScale: newScale)
    }
    
    func applyRotation(degrees: Float) {
        viewModel.applyRotation(degrees: degrees)
    }
    
    func setRotation(degrees: Float) {
        viewModel.setRotation(degrees: degrees)
    }
    
    func applyTranslation(dx: Float, dy: Float) {
        viewModel.applyTranslation(dx: dx, dy: dy)
    }
    
    func addLayer(layer: Layer) {
        viewModel.addLayer(layer: layer)
    }
    
    func removeLayer(layerId: String) {
        viewModel.removeLayer(layerId: layerId)
    }
    
    func selectLayer(layerId: String) {
        viewModel.selectLayer(layerId: layerId)
    }
    
    func deselectLayer() {
        viewModel.deselectLayer()
    }
    
    func getSelectedLayer() -> Layer? {
        return viewModel.getSelectedLayer()
    }
}

// Extension to support StateFlow watching
extension FlowableKt {
    static func watch<T>(_ flowable: Flowable, block: @escaping (T) -> Void) {
        FlowCollectorImpl { (value: Any?) in
            if let value = value as? T {
                block(value)
            }
        }.let { collector in
            FlowableKt.collect(flowable: flowable, collector: collector, completionHandler: { _ in })
        }
    }
}

// Swift implementation of Kotlin's FlowCollector
class FlowCollectorImpl: FlowCollector {
    private let callback: (Any?) -> Void
    
    init(callback: @escaping (Any?) -> Void) {
        self.callback = callback
    }
    
    func emit(value: Any?, completionHandler: @escaping (KotlinUnit?, Error?) -> Void) {
        callback(value)
        completionHandler(KotlinUnit(), nil)
    }
}