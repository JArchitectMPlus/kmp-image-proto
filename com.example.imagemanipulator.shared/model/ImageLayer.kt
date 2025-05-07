package com.example.imagemanipulator.shared.model

class ImageLayer(val image: Any, override val id: String, var scale: Float = 1f, var x: Float = 0f, var y: Float = 0f, var rotation: Float = 0f) : Layer {
    override fun draw() {
    }
}
