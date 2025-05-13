import shared
import SwiftUI
import UIKit
import Foundation

struct LayerControlsView: View {
    @ObservedObject var layerViewModel: LayerViewModelWrapper
    @ObservedObject var canvasViewModel: CanvasViewModelWrapper
    // Use a shared instance of TransformationSystem directly
    private let transformationSystem = TransformationSystem()

    @State private var showingImagePicker = false
    @State private var selectedUIImage: UIImage? = nil
    @State private var showingExportOptions = false
    @State private var showingColorPicker = false
    @State private var exportedBase64: String = ""

    private let imageProvider = IOSImageProvider()

    var body: some View {
        VStack(spacing: 16) {
            Text("Image Manipulator")
                .font(.headline)

            // Layer controls
            layerControlsSection

            // Add layer buttons
            addLayerButtonsSection

            Spacer()
        }
        .sheet(isPresented: $showingImagePicker) {
            imagePicker
        }
    }
    
    // MARK: - View Components

    private var layerControlsSection: some View {
        Group {
            if let layer = layerViewModel.selectedLayer {
                VStack(spacing: 8) {
                    Text("Selected Layer")
                        .font(.subheadline)
                        .fontWeight(.bold)

                    Button("Delete Layer") {
                        deleteLayer(layer)
                    }
                    .padding(.horizontal, 12)
                    .padding(.vertical, 6)
                    .background(Color.red)
                    .foregroundColor(.white)
                    .cornerRadius(6)
                }
                .padding()
                .background(Color.secondary.opacity(0.1))
                .cornerRadius(8)
                .padding(.horizontal)
            } else {
                Text("No layer selected")
                    .foregroundColor(.secondary)
            }
        }
    }

    private var addLayerButtonsSection: some View {
        HStack(spacing: 16) {
            Button("Add Text") {
                addTextLayer()
            }
            .padding(.horizontal, 12)
            .padding(.vertical, 6)
            .background(Color.blue)
            .foregroundColor(.white)
            .cornerRadius(6)

            Button("Add Image") {
                showingImagePicker = true
            }
            .padding(.horizontal, 12)
            .padding(.vertical, 6)
            .background(Color.blue)
            .foregroundColor(.white)
            .cornerRadius(6)
        }
        .padding()
    }

    private var imagePicker: some View {
        NativeImagePicker(selectedImage: $selectedUIImage)
            .onDisappear {
                if let image = selectedUIImage {
                    addImageLayer(image)
                }
            }
    }

    // MARK: - Helper Methods

    private func scaleValue(for layer: Layer) -> String {
        if let imageLayer = layer as? ImageLayer {
            return String(format: "%.1f", imageLayer.scale)
        } else if let textLayer = layer as? TextLayer {
            return String(format: "%.1f", textLayer.scale)
        }
        return "1.0"
    }

    private func rotationValue(for layer: Layer) -> String {
        if let imageLayer = layer as? ImageLayer {
            return String(format: "%.0f°", imageLayer.rotation)
        } else if let textLayer = layer as? TextLayer {
            return String(format: "%.0f°", textLayer.rotation)
        }
        return "0°"
    }
    
    private func decreaseScale(for layer: Layer) {
        if let imageLayer = layer as? ImageLayer {
            let newScale = max(0.1, imageLayer.scale - 0.1)
            // Apply scale directly
            transformationSystem.applyScale(layer: layer, scale: newScale)
            // Update view models
            layerViewModel.updateLayer(layer: imageLayer)
            canvasViewModel.updateLayer(layer: imageLayer)
        } else if let textLayer = layer as? TextLayer {
            let newScale = max(0.1, textLayer.scale - 0.1)
            // Apply scale directly
            transformationSystem.applyScale(layer: layer, scale: newScale)
            // Update view models
            layerViewModel.updateLayer(layer: textLayer)
            canvasViewModel.updateLayer(layer: textLayer)
        }
    }

    private func increaseScale(for layer: Layer) {
        if let imageLayer = layer as? ImageLayer {
            let newScale = min(5.0, imageLayer.scale + 0.1)
            // Apply scale directly
            transformationSystem.applyScale(layer: layer, scale: newScale)
            // Update view models
            layerViewModel.updateLayer(layer: imageLayer)
            canvasViewModel.updateLayer(layer: imageLayer)
        } else if let textLayer = layer as? TextLayer {
            let newScale = min(5.0, textLayer.scale + 0.1)
            // Apply scale directly
            transformationSystem.applyScale(layer: layer, scale: newScale)
            // Update view models
            layerViewModel.updateLayer(layer: textLayer)
            canvasViewModel.updateLayer(layer: textLayer)
        }
    }

    private func resetScale(for layer: Layer) {
        // Apply scale directly with scale 1.0
        transformationSystem.applyScale(layer: layer, scale: 1.0)

        if let imageLayer = layer as? ImageLayer {
            // Update view models
            layerViewModel.updateLayer(layer: imageLayer)
            canvasViewModel.updateLayer(layer: imageLayer)
        } else if let textLayer = layer as? TextLayer {
            // Update view models
            layerViewModel.updateLayer(layer: textLayer)
            canvasViewModel.updateLayer(layer: textLayer)
        }
    }
    
    private func rotateCounterClockwise(for layer: Layer) {
        if let imageLayer = layer as? ImageLayer {
            let newRotation = normalizeAngle(imageLayer.rotation - 15)
            // Apply rotation directly
            transformationSystem.applyRotation(layer: layer, angle: newRotation)
            // Update view models
            layerViewModel.updateLayer(layer: imageLayer)
            canvasViewModel.updateLayer(layer: imageLayer)
        } else if let textLayer = layer as? TextLayer {
            let newRotation = normalizeAngle(textLayer.rotation - 15)
            // Apply rotation directly
            transformationSystem.applyRotation(layer: layer, angle: newRotation)
            // Update view models
            layerViewModel.updateLayer(layer: textLayer)
            canvasViewModel.updateLayer(layer: textLayer)
        }
    }
    
    private func rotateClockwise(for layer: Layer) {
        if let imageLayer = layer as? ImageLayer {
            let newRotation = normalizeAngle(imageLayer.rotation + 15)
            // Apply rotation directly
            transformationSystem.applyRotation(layer: layer, angle: newRotation)
            // Update view models
            layerViewModel.updateLayer(layer: imageLayer)
            canvasViewModel.updateLayer(layer: imageLayer)
        } else if let textLayer = layer as? TextLayer {
            let newRotation = normalizeAngle(textLayer.rotation + 15)
            // Apply rotation directly
            transformationSystem.applyRotation(layer: layer, angle: newRotation)
            // Update view models
            layerViewModel.updateLayer(layer: textLayer)
            canvasViewModel.updateLayer(layer: textLayer)
        }
    }
    
    private func resetRotation(for layer: Layer) {
        // Apply rotation directly with angle 0
        transformationSystem.applyRotation(layer: layer, angle: 0)

        if let imageLayer = layer as? ImageLayer {
            // Update view models
            layerViewModel.updateLayer(layer: imageLayer)
            canvasViewModel.updateLayer(layer: imageLayer)
        } else if let textLayer = layer as? TextLayer {
            // Update view models
            layerViewModel.updateLayer(layer: textLayer)
            canvasViewModel.updateLayer(layer: textLayer)
        }
    }
    
    private func updateText(_ textLayer: TextLayer, newText: String) {
        layerViewModel.setText(text: newText)
        canvasViewModel.updateLayer(layer: textLayer)
    }
    
    private func updateTextColor(_ textLayer: TextLayer, newColor: String?) {
        if let color = newColor {
            layerViewModel.setTextColor(color: color)
            canvasViewModel.updateLayer(layer: textLayer)
        }
    }
    
    private func toggleVisibility(_ layer: Layer, visible: Bool) {
        if let imageLayer = layer as? ImageLayer {
            imageLayer.setVisibility(visible: visible)
            layerViewModel.updateLayer(layer: imageLayer)
            canvasViewModel.updateLayer(layer: imageLayer)
        } else if let textLayer = layer as? TextLayer {
            textLayer.setVisibility(visible: visible)
            layerViewModel.updateLayer(layer: textLayer)
            canvasViewModel.updateLayer(layer: textLayer)
        }
    }
    
    private func deleteLayer(_ layer: Layer) {
        layerViewModel.removeLayer(layer: layer)
        // Since layer.id is a String, not an optional, we can use it directly
        canvasViewModel.removeLayer(layerId: layer.id)
    }
    
    private func addTextLayer() {
        let ts = TransformationSystem()
        let layerId = UUID().uuidString
        let textLayer = TextLayer(
            text: "New Text",
            id: layerId,
            color: "#000000",
            font: "Default",
            curve: 0,
            x: 150,
            y: 150,
            scale: 1.0,
            rotation: 0,
            isVisible: true,
            transformationSystem: ts
        )
        
        canvasViewModel.addLayer(layer: textLayer)
        layerViewModel.addLayer(layer: textLayer)
        layerViewModel.selectLayer(textLayer)
    }
    
    private func addImageLayer(_ image: UIImage) {
        let ts = TransformationSystem()
        let layerId = UUID().uuidString
        let path = "temp://\(layerId)"
        
        let layer = ImageLayer(
            path: path,
            id: layerId,
            image: image,
            scale: 1.0,
            x: 100,
            y: 100,
            rotation: 0,
            isVisible: true,
            transformationSystem: ts
        )
        
        canvasViewModel.addLayer(layer: layer)
        layerViewModel.addLayer(layer: layer)
        layerViewModel.selectLayer(layer)
    }
    
    private func exportToBase64() {
        // Implementation deferred - will be used in future updates
    }
    
    private func clearCanvas() {
        canvasViewModel.clear()
        layerViewModel.clear()
    }
}

struct LayerItemView: View {
    let layer: Layer
    let isSelected: Bool
    let onSelect: () -> Void
    
    var body: some View {
        HStack {
            Text("Layer")
            Spacer()
            Button("Select") {
                onSelect()
            }
        }
    }
    
    private var layerName: String {
        if layer is ImageLayer {
            return "Image Layer"
        } else if layer is TextLayer {
            return "Text Layer"
        } else {
            return "Layer"
        }
    }
}