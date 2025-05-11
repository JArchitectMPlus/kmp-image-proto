package com.example.imagemanipulator.shared.model

/**
 * Interface for all layer types in the application
 * Provides a common contract for layer operations
 */
interface Layer {
    /**
     * Unique identifier for the layer
     */
    val id: String

    /**
     * Draw this layer using the current transformation state
     */
    fun draw()

    /**
     * Check if this layer is visible
     */
    fun isVisible(): Boolean

    /**
     * Apply any pending transformations to this layer
     */
    fun applyTransformations()
}
