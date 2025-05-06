package com.example.imagemanipulator.shared.transformation

import com.example.imagemanipulator.shared.model.Layer
import com.example.imagemanipulator.shared.model.ImageLayer

class PositionTransformation(val x: Float, val y: Float) : TransformationManager() {
    override fun apply(layer: Layer) {
        if (layer is ImageLayer) {
            println("Applying position transformation to ImageLayer: x=$x, y=$y")
        }
    }
    override fun canApply(layer: Layer): Boolean = true
}
