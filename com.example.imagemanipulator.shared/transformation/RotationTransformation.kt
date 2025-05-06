package com.example.imagemanipulator.shared.transformation

class RotationTransformation(val angle: Float) : TransformationManager() {
    override fun apply(layer: Layer) {
        // Implementation to apply rotation transformation
    }
    override fun canApply(layer: Layer): Boolean = true
}
