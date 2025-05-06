package com.example.imagemanipulator.shared.transformation

import com.example.imagemanipulator.shared.model.Layer
import com.example.imagemanipulator.shared.model.TextLayer

class CurveTransformation(val curve: Float) : TransformationManager() {
    override fun apply(layer: Layer) {
        if (layer is TextLayer) {
            // Apply curve transformation to TextLayer
            println("Applying curve transformation with value: $curve")
        }
    }
    override fun canApply(layer: Layer): Boolean = true
}
