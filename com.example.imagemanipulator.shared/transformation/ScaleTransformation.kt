package com.example.imagemanipulator.shared.transformation

import com.example.imagemanipulator.shared.model.Layer

class ScaleTransformation(val scale: Float) : TransformationManager() {
 override fun apply(layer: Layer) {}
 override fun canApply(layer: Layer): Boolean = true
}
