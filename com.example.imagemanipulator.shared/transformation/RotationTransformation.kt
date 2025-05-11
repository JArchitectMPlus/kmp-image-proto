package com.example.imagemanipulator.shared.transformation

import com.example.imagemanipulator.shared.model.ImageLayer
import com.example.imagemanipulator.shared.model.Layer
import com.example.imagemanipulator.shared.model.TextLayer

/**
 * Transformation that applies rotation to a layer
 */
class RotationTransformation(val angle: Float) : TransformationManager() {
    /**
     * Apply rotation transformation to a layer
     */
    override fun apply(layer: Layer) {
        when (layer) {
            is ImageLayer -> {
                layer.rotation = angle
            }
            is TextLayer -> {
                layer.rotation = angle
            }
        }
    }

    /**
     * Rotation transformation can be applied to ImageLayer and TextLayer objects
     */
    override fun canApply(layer: Layer): Boolean =
        layer is ImageLayer || layer is TextLayer
}
