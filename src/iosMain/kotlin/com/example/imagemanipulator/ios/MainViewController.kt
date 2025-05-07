package com.example.imagemanipulator.ios

import androidx.compose.ui.window.ComposeUIViewController
import com.example.imagemanipulator.ios.ui.ImageManipulatorView
import com.example.imagemanipulator.ios.util.ImageHelper
import kotlinx.cinterop.*
import platform.Foundation.*
import platform.UIKit.*
import platform.darwin.*

class MainViewController : UIViewController {
    private val imageHelper = ImageHelper()
    private val imagePicker = UIImagePickerController()
    
    constructor() : super(nibName = null, bundle = null) {
        imagePicker.delegate = createImagePickerDelegate()
        imagePicker.sourceType = UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypePhotoLibrary
    }
    
    override fun viewDidLoad() {
        super.viewDidLoad()
        
        // Set up the Compose UI
        val composeViewController = ComposeUIViewController {
            ImageManipulatorView(onPickImage = {
                presentViewController(imagePicker, true, null)
            })
        }
        
        // Add the Compose view controller as a child
        addChildViewController(composeViewController)
        view.addSubview(composeViewController.view)
        
        // Size and position the view controller's view
        composeViewController.view.setFrame(view.bounds)
        
        // Complete the addition of the child view controller
        composeViewController.didMoveToParentViewController(this)
    }
    
    // Create a delegate for handling image picking
    private fun createImagePickerDelegate(): NSObject {
        return object : NSObject(), UINavigationControllerDelegateProtocol, UIImagePickerControllerDelegateProtocol {
            override fun imagePickerController(
                picker: UIImagePickerController,
                didFinishPickingMediaWithInfo: Map<Any?, *>
            ) {
                // Extract the selected image
                val selectedImage = didFinishPickingMediaWithInfo[UIImagePickerControllerOriginalImage] as? UIImage
                
                // Dismiss the picker
                picker.dismissViewControllerAnimated(true) {
                    // Load the selected image
                    selectedImage?.let { image ->
                        // Handle the image in the Compose UI
                        // This part would be completed when setting up the ability to communicate
                        // between the UIKit parts and Compose parts of the app
                    }
                }
            }
            
            override fun imagePickerControllerDidCancel(picker: UIImagePickerController) {
                picker.dismissViewControllerAnimated(true, null)
            }
        }
    }
}