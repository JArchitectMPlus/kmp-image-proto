package com.example.imagemanipulator.shared.transformation

import com.example.imagemanipulator.shared.model.Layer

/**
 * Abstract base class for all transformation operations
 * Provides a common contract for applying transformations to layers
 */
abstract class TransformationManager {
    /**
     * Apply this transformation to the specified layer
     */
    abstract fun apply(layer: Layer)

    /**
     * Check if this transformation can be applied to the given layer
     */
    abstract fun canApply(layer: Layer): Boolean
}
