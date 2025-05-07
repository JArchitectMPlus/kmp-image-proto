package com.example.imagemanipulator.shared.transformation

import com.example.imagemanipulator.shared.model.ImageLayer
import com.example.imagemanipulator.shared.model.Layer

class RotationTransformation(val angle: Float) : TransformationManager() {
    override fun apply(layer: Layer) {
        if (layer is ImageLayer) {
            layer.rotation = angle
        }
    }
    override fun canApply(layer: Layer): Boolean = true
}
