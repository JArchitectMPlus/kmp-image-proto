import shared
import Foundation
import SwiftUI
import Combine

/// A wrapper class that adapts the Kotlin `CanvasViewModel` to Swift's `ObservableObject` pattern
class CanvasViewModelWrapper: ObservableObject {
    // The underlying Kotlin view model
    private let viewModel: CanvasViewModel

    // Published properties for SwiftUI to observe
    @Published var canvasModel: Canvas
    @Published var layers: [Layer] = []

    // Collectors for Kotlin flow collection
    private var canvasCollector: FlowCollector?
    private var layersCollector: FlowCollector?

    init(viewModel: CanvasViewModel = CanvasViewModel()) {
        self.viewModel = viewModel
        self.canvasModel = viewModel.canvas.value as! Canvas
        setupSubscriptions()
    }

    /// Set up observers for Kotlin StateFlow objects
    private func setupSubscriptions() {
        // Create collectors for each property
        canvasCollector = FlowCollectorImpl { [weak self] value in
            if let canvas = value as? Canvas {
                DispatchQueue.main.async {
                    self?.canvasModel = canvas
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

        // Collect from each flow
        if let canvasCollector = canvasCollector {
            collectFlow(viewModel.canvas, collector: canvasCollector)
        }

        if let layersCollector = layersCollector {
            collectFlow(viewModel.layers, collector: layersCollector)
        }
    }

    // Forward actions to the Kotlin view model

    func addLayer(layer: Layer) {
        viewModel.addLayer(layer: layer)
    }

    func removeLayer(layerId: String) {
        viewModel.removeLayer(layerId: layerId)
    }

    func clear() {
        viewModel.clear()
    }

    func updateLayer(layer: Layer) {
        viewModel.updateLayer(layer: layer)
    }
}

// Using flow collector implementation
