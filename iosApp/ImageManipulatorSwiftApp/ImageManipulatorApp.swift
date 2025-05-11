import SwiftUI
import shared

@main
struct ImageManipulatorApp: App {
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}

struct ContentView: View {
    var body: some View {
        ImageEditorView()
    }
}