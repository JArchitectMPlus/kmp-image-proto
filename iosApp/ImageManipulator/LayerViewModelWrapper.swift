import Foundation
import shared
import SwiftUI
import Combine

/// A wrapper class that adapts the Kotlin `LayerViewModel` to Swift's `ObservableObject` pattern
class LayerViewModelWrapper: ObservableObject {
    // The underlying Kotlin view model
    private let viewModel: LayerViewModel
    
    // Published properties for SwiftUI to observe
    @Published var layers: [Layer] = []
    @Published var selectedLayer: Layer? = nil
    
    init(viewModel: LayerViewModel = LayerViewModel()) {
        self.viewModel = viewModel
        setupSubscriptions()
    }
    
    /// Set up observers for Kotlin StateFlow objects
    private func setupSubscriptions() {
        // Observe layer changes
        viewModel.getLayers().watch { [weak self] layers in
            guard let layers = layers as? [Layer] else { return }
            DispatchQueue.main.async {
                self?.layers = layers
            }
        }
        
        // Observe selected layer changes
        viewModel.getSelectedLayer().watch { [weak self] layer in
            DispatchQueue.main.async {
                self?.selectedLayer = layer as? Layer
            }
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