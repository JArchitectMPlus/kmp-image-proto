package com.example.imagemanipulator.shared.transformation

import com.example.imagemanipulator.shared.model.Layer
import com.example.imagemanipulator.shared.model.ImageLayer
import kotlin.reflect.KProperty1

class ScaleTransformation(val scale: Float) : TransformationManager() {
    override fun apply(layer: Layer) {
        if (layer is ImageLayer) {
            (layer::class.members.first { it.name == "scale" } as KProperty1<ImageLayer, Float>).getSetter(layer).call(scale)
        }
  }
    override fun canApply(layer: Layer): Boolean = true
}
