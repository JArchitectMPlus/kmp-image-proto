import SwiftUI
import shared

struct MainEditorView: View {
    @StateObject private var canvasViewModel = CanvasViewModelWrapper()
    @StateObject private var layerViewModel = LayerViewModelWrapper()
    @StateObject private var viewModel = ImageViewModelWrapper()
    
    var body: some View {
        TabView {
            // Image Editor Tab
            ImageEditorView()
                .tabItem {
                    Label("Image Editor", systemImage: "photo")
                }
            
            // Layer Controls Tab
            SimpleLayerControlsView(layerViewModel: layerViewModel, canvasViewModel: canvasViewModel)
                .tabItem {
                    Label("Layers", systemImage: "square.on.square")
                }
        }
    }
}

// Preview
struct MainEditorView_Previews: PreviewProvider {
    static var previews: some View {
        MainEditorView()
    }
}