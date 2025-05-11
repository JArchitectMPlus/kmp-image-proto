package com.example.imagemanipulator.shared.transformation

import com.example.imagemanipulator.shared.model.Layer
import com.example.imagemanipulator.shared.model.ImageLayer
import com.example.imagemanipulator.shared.model.TextLayer

/**
 * Transformation that positions a layer at specific coordinates
 */
class PositionTransformation(val x: Float, val y: Float) : TransformationManager() {
    /**
     * Apply position transformation to a layer
     */
    override fun apply(layer: Layer) {
        when (layer) {
            is ImageLayer -> {
                layer.x = x
                layer.y = y
            }
            is TextLayer -> {
                layer.x = x
                layer.y = y
            }
        }
    }

    /**
     * Position transformation can be applied to ImageLayer and TextLayer objects
     */
    override fun canApply(layer: Layer): Boolean =
        layer is ImageLayer || layer is TextLayer
}
