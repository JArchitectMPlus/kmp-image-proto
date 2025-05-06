package com.example.imagemanipulator.shared.model

class TextLayer(
    val text: String,
    val color: String,
    val font: String,
    val curve: Float
) : Layer {
    override val id: String = java.util.UUID.randomUUID().toString()

    override fun draw() {
        // Implementation for drawing text layer
    }
}
