package com.example.imagemanipulator.shared.model

import com.example.imagemanipulator.shared.transformation.TransformationSystem

class TextLayer(
    val text: String,
    override val id: String,
    val color: String,
    val font: String,
    val curve: Float,
    var x: Float = 100f,
    var y: Float = 100f,
    var scale: Float = 1f,
    var rotation: Float = 0f,
    private var isVisible: Boolean = true,
    private val transformationSystem: TransformationSystem? = null
) : Layer {
    override fun draw() {
        // Implementation for drawing text layer
    }

    override fun isVisible(): Boolean = isVisible

    fun setVisibility(visible: Boolean) {
        isVisible = visible
    }

    override fun applyTransformations() {
        transformationSystem?.applyTransformations(this)
    }

    /**
     * Clone this text layer
     */
    fun clone(newId: String): TextLayer {
        return TextLayer(
            text = this.text,
            id = newId,
            color = this.color,
            font = this.font,
            curve = this.curve,
            x = this.x,
            y = this.y,
            scale = this.scale,
            rotation = this.rotation,
            isVisible = this.isVisible,
            transformationSystem = this.transformationSystem
        )
    }
}
