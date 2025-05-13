import shared
import Foundation
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

    // Collectors for Kotlin flow collection
    private var scaleCollector: FlowCollector?
    private var rotationCollector: FlowCollector?
    private var layersCollector: FlowCollector?
    private var selectedLayerIdCollector: FlowCollector?

    init(viewModel: ImageViewModel = ImageViewModel()) {
        self.viewModel = viewModel
        setupSubscriptions()
    }

    /// Set up observers for Kotlin StateFlow objects
    private func setupSubscriptions() {
        // Create collectors for each property
        scaleCollector = FlowCollectorImpl { [weak self] value in
            if let scale = value as? Float {
                DispatchQueue.main.async {
                    self?.scale = scale
                }
            }
        }

        rotationCollector = FlowCollectorImpl { [weak self] value in
            if let rotation = value as? Float {
                DispatchQueue.main.async {
                    self?.rotation = rotation
                }
            }
        }

        layersCollector = FlowCollectorImpl { [weak self] value in
            if let layers = value as? [Layer] {
                DispatchQueue.main.async {
                    self?.layers = layers
                }
            }
        }

        selectedLayerIdCollector = FlowCollectorImpl { [weak self] value in
            if let selectedId = value as? String? {
                DispatchQueue.main.async {
                    self?.selectedLayerId = selectedId
                }
            }
        }

        // Collect from each flow
        if let scaleCollector = scaleCollector {
            collectFlow(viewModel.scale, collector: scaleCollector)
        }

        if let rotationCollector = rotationCollector {
            collectFlow(viewModel.rotation, collector: rotationCollector)
        }

        if let layersCollector = layersCollector {
            collectFlow(viewModel.layers, collector: layersCollector)
        }

        if let selectedLayerIdCollector = selectedLayerIdCollector {
            collectFlow(viewModel.selectedLayerId, collector: selectedLayerIdCollector)
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

    // Additional methods for ImageEditorView
    @Published var selectedImage: UIImage?

    func updateScale(scale: Float) {
        setScale(newScale: scale)
    }

    func updateRotation(degrees: Float) {
        setRotation(degrees: degrees)
    }

    func updateOffset(x: Float, y: Float) {
        applyTranslation(dx: x, dy: y)
    }

    func resetAll() {
        resetTransformations()
        selectedImage = nil
    }
}

// Using flow collector implementation
