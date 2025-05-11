package com.example.imagemanipulator.shared.transformation

import com.example.imagemanipulator.shared.model.ImageLayer
import com.example.imagemanipulator.shared.model.Layer
import com.example.imagemanipulator.shared.model.TextLayer

/**
 * A unified system for applying transformations to layers across platforms.
 * This ensures consistent behavior between Android and iOS.
 */
class TransformationSystem {
    // Store active transformations
    private val transformations = mutableListOf<TransformationManager>()
    
    /**
     * Apply a position transformation to a layer
     */
    fun applyPosition(layer: Layer, x: Float, y: Float) {
        if (layer is ImageLayer) {
            layer.x = x
            layer.y = y
        } else if (layer is TextLayer) {
            layer.x = x
            layer.y = y
        }
    }
    
    /**
     * Apply a rotation transformation to a layer
     */
    fun applyRotation(layer: Layer, angle: Float) {
        if (layer is ImageLayer) {
            layer.rotation = angle
        } else if (layer is TextLayer) {
            layer.rotation = angle
        }
    }
    
    /**
     * Apply a scale transformation to a layer
     */
    fun applyScale(layer: Layer, scale: Float) {
        if (layer is ImageLayer) {
            layer.scale = scale
        } else if (layer is TextLayer) {
            layer.scale = scale
        }
    }
    
    /**
     * Apply all transformations to a layer
     */
    fun applyTransformations(layer: Layer) {
        transformations.forEach { transformation ->
            if (transformation.canApply(layer)) {
                transformation.apply(layer)
            }
        }
    }
    
    /**
     * Add a transformation to the system
     */
    fun addTransformation(transformation: TransformationManager) {
        transformations.add(transformation)
    }
    
    /**
     * Remove a transformation from the system
     */
    fun removeTransformation(transformation: TransformationManager) {
        transformations.remove(transformation)
    }

    /**
     * Clear all transformations
     */
    fun clearTransformations() {
        transformations.clear()
    }
    
    /**
     * Reset all transformations for a layer
     */
    fun resetTransformations(layer: Layer) {
        if (layer is ImageLayer) {
            layer.x = 100f
            layer.y = 100f
            layer.rotation = 0f
            layer.scale = 1.0f
        } else if (layer is TextLayer) {
            layer.x = 100f
            layer.y = 100f
            layer.rotation = 0f
            layer.scale = 1.0f
        }
    }
}