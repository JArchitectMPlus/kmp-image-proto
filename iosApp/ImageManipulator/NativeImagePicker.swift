import shared
import SwiftUI
import PhotosUI

struct NativeImagePicker: UIViewControllerRepresentable {
    @Binding var selectedImage: UIImage?
    @Environment(\.presentationMode) private var presentationMode
    
    class Coordinator: NSObject, PHPickerViewControllerDelegate {
        var parent: NativeImagePicker

        init(_ parent: NativeImagePicker) {
            self.parent = parent
        }
        
        func picker(_ picker: PHPickerViewController, didFinishPicking results: [PHPickerResult]) {
            parent.presentationMode.wrappedValue.dismiss()
            
            guard let provider = results.first?.itemProvider else { return }
            if provider.canLoadObject(ofClass: UIImage.self) {
                provider.loadObject(ofClass: UIImage.self) { image, error in
                    DispatchQueue.main.async {
                        self.parent.selectedImage = image as? UIImage
                    }
                }
            }
        }
    }
    
    func makeUIViewController(context: Context) -> PHPickerViewController {
        var config = PHPickerConfiguration()
        config.filter = .images
        
        let picker = PHPickerViewController(configuration: config)
        picker.delegate = context.coordinator
        return picker
    }
    
    func updateUIViewController(_ uiViewController: PHPickerViewController, context: Context) {}
    
    func makeCoordinator() -> Coordinator {
        Coordinator(self)
    }
}

// Helper for presenting the image picker
extension View {
    func nativeImagePicker(selectedImage: Binding<UIImage?>, isPresented: Binding<Bool>) -> some View {
        self.sheet(isPresented: isPresented) {
            NativeImagePicker(selectedImage: selectedImage)
        }
    }
}