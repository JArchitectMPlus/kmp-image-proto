package com.example.imagemanipulator.shared.transformation

import com.example.imagemanipulator.shared.model.Layer
import com.example.imagemanipulator.shared.model.ImageLayer
import com.example.imagemanipulator.shared.model.TextLayer

/**
 * Transformation that applies scaling to a layer
 */
class ScaleTransformation(val scale: Float) : TransformationManager() {
    /**
     * Apply scale transformation to a layer
     */
    override fun apply(layer: Layer) {
        when (layer) {
            is ImageLayer -> {
                layer.scale = scale
            }
            is TextLayer -> {
                layer.scale = scale
            }
        }
    }

    /**
     * Scale transformation can be applied to ImageLayer and TextLayer objects
     */
    override fun canApply(layer: Layer): Boolean =
        layer is ImageLayer || layer is TextLayer
}
