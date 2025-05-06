package com.example.imagemanipulator.shared.model

class Canvas(
    val width: Int,
    val height: Int
) {
    val layers: MutableList<Layer> = mutableListOf()

    fun addLayer(layer: Layer) {
        layers.add(layer)
    }

    fun removeLayer(layer: Layer) {
        layers.remove(layer)
    }
}
