import shared
import SwiftUI
import UIKit
import Foundation

// A simplified version of the LayerControlsView
struct SimpleLayerControlsView: View {
    @ObservedObject var layerViewModel: LayerViewModelWrapper
    @ObservedObject var canvasViewModel: CanvasViewModelWrapper
    
    @State private var showingImagePicker = false
    @State private var selectedUIImage: UIImage? = nil
    
    private let imageProvider = IOSImageProvider()
    
    var body: some View {
        VStack(spacing: 16) {
            Text("Image Manipulator")
                .font(.headline)
                .padding()
            
            // Layer management buttons
            HStack(spacing: 16) {
                Button("Add Text") {
                    addTextLayer()
                }
                .buttonStyle(PrimaryButtonStyle())
                
                Button("Add Image") {
                    showingImagePicker = true
                }
                .buttonStyle(PrimaryButtonStyle())
                .sheet(isPresented: $showingImagePicker) {
                    NativeImagePicker(selectedImage: $selectedUIImage)
                        .onDisappear {
                            if let image = selectedUIImage {
                                addImageLayer(image)
                            }
                        }
                }
            }
            .padding()
            
            // Current layer info
            if let selectedLayer = layerViewModel.selectedLayer {
                VStack(alignment: .leading) {
                    Text("Selected Layer: \(getLayerName(selectedLayer))")
                        .font(.subheadline)
                        .padding(.bottom, 4)
                    
                    // Basic controls
                    HStack {
                        Button("Delete Layer") {
                            layerViewModel.removeLayer(layer: selectedLayer)
                            canvasViewModel.removeLayer(layerId: selectedLayer.id)
                        }
                        .buttonStyle(DestructiveButtonStyle())
                    }
                    .padding(.bottom)
                }
                .padding()
                .background(Color.secondary.opacity(0.1))
                .cornerRadius(8)
                .padding(.horizontal)
            } else {
                Text("No layer selected")
                    .font(.subheadline)
                    .foregroundColor(.secondary)
                    .padding()
            }
            
            // Layer list
            ScrollView {
                VStack(spacing: 8) {
                    ForEach(Array(layerViewModel.layers.enumerated()), id: \.element.id) { _, layer in
                        layerRow(for: layer)
                    }
                }
                .padding()
            }
            
            Spacer()
        }
    }
    
    // Helper to generate layer name
    private func getLayerName(_ layer: Layer) -> String {
        if layer is TextLayer {
            return "Text Layer \(String(describing: layer.id.prefix(4)))"
        } else {
            return "Image Layer \(String(describing: layer.id.prefix(4)))"
        }
    }
    
    // Layer row view
    private func layerRow(for layer: Layer) -> some View {
        let isSelected = layerViewModel.selectedLayer?.id == layer.id
        
        return HStack {
            Text(getLayerName(layer))
                .fontWeight(isSelected ? .bold : .regular)
            
            Spacer()
            
            Button("Select") {
                layerViewModel.selectLayer(layer)
            }
            .buttonStyle(PrimaryButtonStyle())
        }
        .padding()
        .background(isSelected ? Color.blue.opacity(0.2) : Color.clear)
        .cornerRadius(8)
    }
    
    // Methods for layer management
    private func addTextLayer() {
        // Create a new transformation system for the layer
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
        // Create a new transformation system for the layer
        let ts = TransformationSystem()
        
        // Create a unique ID for the layer
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
}

// Custom button styles
struct PrimaryButtonStyle: ButtonStyle {
    func makeBody(configuration: Configuration) -> some View {
        configuration.label
            .padding(.horizontal, 12)
            .padding(.vertical, 6)
            .background(Color.blue)
            .foregroundColor(.white)
            .cornerRadius(6)
            .scaleEffect(configuration.isPressed ? 0.95 : 1)
    }
}

struct SecondaryButtonStyle: ButtonStyle {
    func makeBody(configuration: Configuration) -> some View {
        configuration.label
            .padding(.horizontal, 12)
            .padding(.vertical, 6)
            .background(Color.blue.opacity(0.2))
            .foregroundColor(.blue)
            .cornerRadius(6)
            .scaleEffect(configuration.isPressed ? 0.95 : 1)
    }
}

struct DestructiveButtonStyle: ButtonStyle {
    func makeBody(configuration: Configuration) -> some View {
        configuration.label
            .padding(.horizontal, 12)
            .padding(.vertical, 6)
            .background(Color.red)
            .foregroundColor(.white)
            .cornerRadius(6)
            .scaleEffect(configuration.isPressed ? 0.95 : 1)
    }
}