package com.example.imagemanipulator.shared.ui

import androidx.compose.runtime.State
import com.example.imagemanipulator.shared.transformation.Point

/**
 * Interface for platform-specific image provider implementations.
 * This allows for a common API across platforms while allowing platform-specific implementations.
 */
interface PlatformImageProvider {
    // Current image state
    val currentImageBitmap: State<Any?>
    
    // Transformation states
    val scale: State<Float>
    val rotation: State<Float>
    val offset: State<Point>
    
    // Text overlays
    val textOverlays: State<List<TextOverlay>>
    
    // Launch the platform-specific image picker
    fun pickImage()
    
    // Apply transformations
    fun applyScale(scale: Float)
    fun applyRotation(rotation: Float)
    fun applyTranslation(dx: Float, dy: Float)
    
    // Set transformation values directly
    fun setScale(scale: Float)
    fun setRotation(rotation: Float)
    
    // Reset transformations
    fun resetTransformations()
    
    // Overlay handling
    fun addTextOverlay(text: String, position: Point)
    fun removeTextOverlay(id: Long)
    
    // Export functionality
    fun exportToBase64(callback: (String?) -> Unit)
}