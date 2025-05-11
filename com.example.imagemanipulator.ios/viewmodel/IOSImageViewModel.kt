package com.example.imagemanipulator.ios.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.example.imagemanipulator.shared.ImageViewModel
import com.example.imagemanipulator.shared.LayerViewModel
import com.example.imagemanipulator.shared.model.ImageLayer
import com.example.imagemanipulator.shared.model.Layer
import com.example.imagemanipulator.ios.util.KorIMWrapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import platform.UIKit.UIImage
import platform.Foundation.NSURL
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDate

/**
 * iOS-specific implementation of the shared ImageViewModel
 * This class adds iOS-specific functionality like UIImage handling
 *
 * Note: This extends ImageViewModel for shared transformation logic
 * but we'll need to use a separate instance of LayerViewModel for layer operations
 */
@ExperimentalForeignApi
class IOSImageViewModel : ImageViewModel() {
    // Create a LayerViewModel instance for layer operations
    private val layerViewModel = LayerViewModel()
    // iOS-specific image state for UIImage
    private val _uiImage = MutableStateFlow<UIImage?>(null)
    val uiImage: StateFlow<UIImage?> = _uiImage.asStateFlow()

    // Platform-specific image loading
    private val korIMWrapper = KorIMWrapper()
    
    // Whether any loading operation is in progress
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    /**
     * Set a new image and reset transformations
     */
    fun setImage(newImage: UIImage?) {
        _uiImage.value = newImage
        resetTransformations()
    }

    /**
     * Add a UIImage as a new layer
     */
    suspend fun addUIImageAsLayer(image: UIImage, path: String): String {
        val layerId = generateLayerId()
        val layer = ImageLayer(
            path = path,
            id = layerId,
            image = image
        )

        // Add the layer to the shared ViewModel
        addLayer(layer)

        return layerId
    }
    
    /**
     * Generate a unique layer ID
     */
    private fun generateLayerId(): String {
        val timestamp = NSDate().description
        val random = (kotlin.random.Random.nextDouble() * 10000).toInt()
        return "layer_${timestamp}_$random"
    }

    /**
     * Get the UIImage from the specified layer
     */
    fun getLayerUIImage(layerId: String): UIImage? {
        val layer = layers.value.find { it.id == layerId }
        return if (layer is ImageLayer) {
            layer.image as? UIImage
        } else {
            null
        }
    }

    /**
     * Load an image from a URL
     */
    @ExperimentalForeignApi
    suspend fun loadImageFromURL(url: NSURL): UIImage? {
        _isLoading.value = true
        try {
            val path = url.path ?: return null
            return korIMWrapper.load(path) as? UIImage
        } finally {
            _isLoading.value = false
        }
    }
    
    /**
     * Load a bundled image
     */
    @ExperimentalForeignApi
    suspend fun loadBundledImage(imageName: String): UIImage? {
        _isLoading.value = true
        try {
            return korIMWrapper.load(imageName) as? UIImage
        } finally {
            _isLoading.value = false
        }
    }
    
    /**
     * Create a custom scale updater for iOS
     */
    fun updateScaleForSelectedLayer(newScale: Float) {
        // Use the parent class's method
        setScale(newScale)
        
        // Get the selected layer
        val selectedLayer = getSelectedLayer()
        
        // If we have a selected layer, update it directly too
        if (selectedLayer is ImageLayer) {
            // Create a new layer with the updated scale
            val updatedLayer = ImageLayer(
                id = selectedLayer.id,
                path = selectedLayer.path,
                image = selectedLayer.image,
                scale = newScale,
                x = selectedLayer.x,
                y = selectedLayer.y,
                rotation = selectedLayer.rotation,
                isVisible = selectedLayer.isVisible()
            )
            
            // Use the layerViewModel to update the layer
            layerViewModel.updateLayer(updatedLayer)
        }
    }
    
    /**
     * Create a custom rotation updater for iOS
     */
    fun updateRotationForSelectedLayer(newRotation: Float) {
        // Use the parent class's method
        setRotation(newRotation)
        
        // Get the selected layer
        val selectedLayer = getSelectedLayer()
        
        // If we have a selected layer, update it directly too
        if (selectedLayer is ImageLayer) {
            // Create a new layer with the updated rotation
            val updatedLayer = ImageLayer(
                id = selectedLayer.id,
                path = selectedLayer.path,
                image = selectedLayer.image,
                scale = selectedLayer.scale,
                x = selectedLayer.x,
                y = selectedLayer.y,
                rotation = newRotation,
                isVisible = selectedLayer.isVisible()
            )
            
            // Use the layerViewModel to update the layer
            layerViewModel.updateLayer(updatedLayer)
        }
    }
    
    /**
     * Create a custom position updater for iOS
     */
    fun updatePositionForSelectedLayer(deltaX: Float, deltaY: Float) {
        // Use the parent class's method
        applyTranslation(deltaX, deltaY)
        
        // Get the selected layer
        val selectedLayer = getSelectedLayer()
        
        // If we have a selected layer, update it directly too
        if (selectedLayer is ImageLayer) {
            // Create a new layer with the updated position
            val updatedLayer = ImageLayer(
                id = selectedLayer.id,
                path = selectedLayer.path,
                image = selectedLayer.image,
                scale = selectedLayer.scale,
                x = selectedLayer.x + deltaX,
                y = selectedLayer.y + deltaY,
                rotation = selectedLayer.rotation,
                isVisible = selectedLayer.isVisible()
            )
            
            // Use the layerViewModel to update the layer
            layerViewModel.updateLayer(updatedLayer)
        }
    }
}