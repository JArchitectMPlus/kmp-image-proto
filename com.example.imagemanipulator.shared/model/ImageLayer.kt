package com.example.imagemanipulator.shared.model

import com.example.imagemanipulator.shared.util.KorIMWrapper
import com.example.imagemanipulator.shared.transformation.TransformationSystem

/**
 * A layer that contains an image with transformation capabilities
 */
class ImageLayer(
    val path: String,
    override val id: String,
    var image: Any? = null,
    var scale: Float = 1f,
    var x: Float = 0f,
    var y: Float = 0f,
    var rotation: Float = 0f,
    private var isVisible: Boolean = true,
    private val transformationSystem: TransformationSystem? = null
) : Layer {

    /**
     * Draw this image layer
     * The actual drawing implementation will be provided by platform-specific code
     */
    override fun draw() {
        // This is intentionally empty - platform-specific drawing will be implemented
        // by the respective platform's canvas implementation
    }

    /**
     * Check if this layer is visible
     */
    override fun isVisible(): Boolean = isVisible

    /**
     * Set the visibility of this layer
     */
    fun setVisibility(visible: Boolean) {
        isVisible = visible
    }

    /**
     * Apply all pending transformations to this layer
     */
    override fun applyTransformations() {
        transformationSystem?.applyTransformations(this)
    }

    /**
     * Load the image from the specified path
     */
    suspend fun loadImage(korIMWrapper: KorIMWrapper) {
        this.image = korIMWrapper.load(path)
    }

    /**
     * Clone this image layer
     */
    fun clone(newId: String): ImageLayer {
        return ImageLayer(
            path = this.path,
            id = newId,
            image = this.image,
            scale = this.scale,
            x = this.x,
            y = this.y,
            rotation = this.rotation,
            isVisible = this.isVisible,
            transformationSystem = this.transformationSystem
        )
    }
}