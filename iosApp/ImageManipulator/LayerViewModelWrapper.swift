import shared
import Foundation
import SwiftUI
import Combine

/// A wrapper class that adapts the Kotlin `LayerViewModel` to Swift's `ObservableObject` pattern
class LayerViewModelWrapper: ObservableObject {
    // The underlying Kotlin view model
    private let viewModel: LayerViewModel

    // Published properties for SwiftUI to observe
    @Published var layers: [Layer] = []
    @Published var selectedLayer: Layer? = nil

    // Collectors for Kotlin flow collection
    private var layersCollector: FlowCollector?
    private var selectedLayerCollector: FlowCollector?

    init(viewModel: LayerViewModel = LayerViewModel()) {
        self.viewModel = viewModel
        setupSubscriptions()
    }

    /// Set up observers for Kotlin StateFlow objects
    private func setupSubscriptions() {
        // Create collectors for each property
        layersCollector = FlowCollectorImpl { [weak self] value in
            if let layerArray = value as? [Layer] {
                DispatchQueue.main.async {
                    self?.layers = layerArray
                }
            }
        }

        selectedLayerCollector = FlowCollectorImpl { [weak self] value in
            DispatchQueue.main.async {
                self?.selectedLayer = value as? Layer
            }
        }

        // Collect from each flow
        if let layersCollector = layersCollector {
            collectFlow(viewModel.layers, collector: layersCollector)
        }

        if let selectedLayerCollector = selectedLayerCollector {
            collectFlow(viewModel.selectedLayer, collector: selectedLayerCollector)
        }
    }

    // Forward actions to the Kotlin view model

    func addLayer(layer: Layer) {
        viewModel.addLayer(layer: layer)
    }

    func removeLayer(layer: Layer) {
        viewModel.removeLayer(layer: layer)
    }

    func duplicateLayer(layer: Layer) {
        viewModel.duplicateLayer(layer: layer)
    }

    func selectLayer(_ layer: Layer?) {
        viewModel.selectLayer(layer: layer)
    }

    func deleteSelectedLayer() {
        viewModel.deleteSelectedLayer()
    }

    func clear() {
        viewModel.clear()
    }

    func setRotation(_ rotation: Float) {
        viewModel.setRotation(rotation: rotation)
    }

    func setScale(_ scale: Float) {
        viewModel.setScale(scale: scale)
    }

    func setPosition(x: Float, y: Float) {
        viewModel.setPosition(x: x, y: y)
    }

    func toggleVisibility() {
        viewModel.toggleVisibility()
    }

    func updateLayer(layer: Layer) {
        viewModel.updateLayer(layer: layer)
    }

    func setText(text: String) {
        viewModel.setText(text: text)
    }

    func setTextColor(color: String) {
        viewModel.setTextColor(color: color)
    }
}

// Using flow collector implementation
