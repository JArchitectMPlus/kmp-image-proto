package com.example.imagemanipulator.shared.transformation

import com.example.imagemanipulator.shared.model.Layer

class CurveTransformation(val curve: Float) : TransformationManager() {
    override fun apply(layer: Layer) {}
    override fun canApply(layer: Layer): Boolean = true
}
