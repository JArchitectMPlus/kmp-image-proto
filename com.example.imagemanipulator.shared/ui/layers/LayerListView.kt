package com.example.imagemanipulator.shared.ui.layers

/**
 * Base interface for layer list view
 * Each platform will provide its own implementation
 */
interface LayerListView {
    fun show()
    fun hide()
    fun refresh()
}