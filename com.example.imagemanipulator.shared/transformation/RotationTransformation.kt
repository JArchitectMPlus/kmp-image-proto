package com.example.imagemanipulator.shared.transformation

class RotationTransformation(val angle: Float) : TransformationManager() {
    override fun apply(layer: Layer) {
        if (layer is ImageLayer) {
            println("Applying rotation transformation with angle: $angle")
        }
    }
    override fun canApply(layer: Layer): Boolean = true
}
