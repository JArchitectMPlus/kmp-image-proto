import SwiftUI
import shared
import PhotosUI

struct ImageEditorView: View {
    @StateObject private var canvasViewModel = CanvasViewModelWrapper()
    @StateObject private var layerViewModel = LayerViewModelWrapper()
    @StateObject private var viewModel = ImageViewModelWrapper()
    @State private var showImagePicker = false
    @State private var currentScale: CGFloat = 1.0
    @State private var lastScale: CGFloat = 1.0
    @State private var currentRotation: Angle = .degrees(0)
    @State private var lastRotation: Angle = .degrees(0)
    @State private var dragOffset: CGSize = .zero
    
    var body: some View {
        VStack {
            Text("DP Image Editor")
                .font(.largeTitle)
                .fontWeight(.bold)
                .padding()
            
            // Image display area
            ZStack {
                Color(UIColor.lightGray)
                    .cornerRadius(12)
                
                if let uiImage = viewModel.selectedImage {
                    Image(uiImage: uiImage)
                        .resizable()
                        .scaledToFit()
                        .padding(20)
                        .scaleEffect(currentScale)
                        .rotationEffect(currentRotation)
                        .offset(x: dragOffset.width, y: dragOffset.height)
                        .gesture(
                            DragGesture()
                                .onChanged { value in
                                    self.dragOffset = value.translation
                                }
                                .onEnded { value in
                                    self.dragOffset = value.translation
                                    viewModel.updateOffset(x: Float(dragOffset.width), 
                                                          y: Float(dragOffset.height))
                                }
                        )
                        .gesture(
                            MagnificationGesture()
                                .onChanged { value in
                                    let delta = value / lastScale
                                    lastScale = value
                                    currentScale *= delta
                                }
                                .onEnded { value in
                                    lastScale = 1.0
                                    viewModel.updateScale(scale: Float(currentScale))
                                }
                        )
                        .gesture(
                            RotationGesture()
                                .onChanged { angle in
                                    let delta = angle - lastRotation
                                    lastRotation = angle
                                    currentRotation += delta
                                }
                                .onEnded { angle in
                                    lastRotation = .degrees(0)
                                    viewModel.updateRotation(degrees: Float(currentRotation.degrees))
                                }
                        )
                } else {
                    Text("No image loaded")
                        .foregroundColor(.white)
                }
            }
            .frame(height: 400)
            .padding()
            
            // Controls section
            VStack(spacing: 20) {
                // Scale controls
                HStack {
                    Text("Scale: \(String(format: "%.1f", currentScale))")
                    Spacer()
                    Button(action: {
                        currentScale = max(0.5, currentScale - 0.1)
                        viewModel.updateScale(scale: Float(currentScale))
                    }) {
                        Image(systemName: "minus.circle")
                            .font(.title2)
                    }
                    
                    Button(action: {
                        currentScale = min(3.0, currentScale + 0.1)
                        viewModel.updateScale(scale: Float(currentScale))
                    }) {
                        Image(systemName: "plus.circle")
                            .font(.title2)
                    }
                    
                    Button(action: {
                        currentScale = 1.0
                        viewModel.updateScale(scale: 1.0)
                    }) {
                        Image(systemName: "arrow.counterclockwise.circle")
                            .font(.title2)
                    }
                }
                
                // Rotation controls
                HStack {
                    Text("Rotation: \(Int(currentRotation.degrees))°")
                    Spacer()
                    Button(action: {
                        currentRotation = currentRotation - .degrees(15)
                        viewModel.updateRotation(degrees: Float(currentRotation.degrees))
                    }) {
                        Image(systemName: "rotate.left")
                            .font(.title2)
                    }
                    
                    Button(action: {
                        currentRotation = currentRotation + .degrees(15)
                        viewModel.updateRotation(degrees: Float(currentRotation.degrees))
                    }) {
                        Image(systemName: "rotate.right")
                            .font(.title2)
                    }
                    
                    Button(action: {
                        currentRotation = .degrees(0)
                        viewModel.updateRotation(degrees: 0)
                    }) {
                        Image(systemName: "arrow.counterclockwise.circle")
                            .font(.title2)
                    }
                }
                
                // Reset button
                Button("Reset All") {
                    resetAll()
                }
                .frame(maxWidth: .infinity)
                .padding()
                .background(Color.blue)
                .foregroundColor(.white)
                .cornerRadius(10)
            }
            .padding()
            
            // Load Image button
            Button("Load Image") {
                showImagePicker = true
            }
            .frame(maxWidth: .infinity)
            .padding()
            .background(Color.blue)
            .foregroundColor(.white)
            .cornerRadius(10)
            .padding(.horizontal)
            .padding(.bottom)
        }
        .sheet(isPresented: $showImagePicker) {
            NativeImagePicker(selectedImage: $viewModel.selectedImage)
        }
    }
    
    private func resetAll() {
        currentScale = 1.0
        currentRotation = .degrees(0)
        dragOffset = .zero
        viewModel.resetAll()
    }
}

struct ImageEditorView_Previews: PreviewProvider {
    static var previews: some View {
        ImageEditorView()
    }
}