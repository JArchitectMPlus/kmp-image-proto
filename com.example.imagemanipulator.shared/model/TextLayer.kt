package com.example.imagemanipulator.shared.model

class TextLayer(
    val text: String,
    override val id: String,
    val color: String, val font: String, val curve: Float
) : Layer {
    override fun draw() {
        // Implementation for drawing text layer
    }
}
