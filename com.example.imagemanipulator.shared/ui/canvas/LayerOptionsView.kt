package com.example.imagemanipulator.shared.ui.canvas

/**
 * Base interface for layer options view
 * Each platform will provide its own implementation
 */
interface LayerOptionsView {
    fun show(layerId: String)
    fun hide()
    fun isVisible(): Boolean
}