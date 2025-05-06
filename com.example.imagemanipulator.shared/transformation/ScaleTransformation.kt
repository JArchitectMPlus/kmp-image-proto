package com.example.imagemanipulator.shared.transformation

import com.example.imagemanipulator.shared.model.Layer
import com.example.imagemanipulator.shared.model.ImageLayer

class ScaleTransformation(val scale: Float) : TransformationManager() {
 override fun apply(layer: Layer) {
 if (layer is ImageLayer) {
 println("Applying scale transformation with scale: $scale")
 }
  }
 override fun canApply(layer: Layer): Boolean = true
}
