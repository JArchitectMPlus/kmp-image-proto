import Foundation
import shared
import SwiftUI
import Combine

/// A wrapper class that adapts the Kotlin `CanvasViewModel` to Swift's `ObservableObject` pattern
class CanvasViewModelWrapper: ObservableObject {
    // The underlying Kotlin view model
    private let viewModel: CanvasViewModel
    
    // Published properties for SwiftUI to observe
    @Published var canvas: Canvas
    @Published var layers: [Layer] = []
    
    init(viewModel: CanvasViewModel = CanvasViewModel()) {
        self.viewModel = viewModel
        self.canvas = viewModel.getCanvas().value as! Canvas
        setupSubscriptions()
    }
    
    /// Set up observers for Kotlin StateFlow objects
    private func setupSubscriptions() {
        // Observe canvas changes
        viewModel.getCanvas().watch { [weak self] canvas in
            guard let canvas = canvas as? Canvas else { return }
            DispatchQueue.main.async {
                self?.canvas = canvas
            }
        }
        
        // Observe layer changes
        viewModel.getLayers().watch { [weak self] layers in
            guard let layers = layers as? [Layer] else { return }
            DispatchQueue.main.async {
                self?.layers = layers
            }
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