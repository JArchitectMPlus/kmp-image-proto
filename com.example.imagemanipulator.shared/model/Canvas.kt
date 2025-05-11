package com.example.imagemanipulator.shared.model

class Canvas(
    val width: Int = 1080,
    val height: Int = 1920,
    val aspectRatio: String = "9:16",
    val layers: MutableList<Layer> = mutableListOf()
) {
    fun addLayer(layer: Layer) {
        if (!layers.contains(layer)) layers.add(layer)
    }

    fun removeLayer(layer: Layer) {
        layers.remove(layer)
    }

    fun removeLayerById(layerId: String) {
        layers.removeAll { it.id == layerId }
    }

    fun clear() {
        layers.clear()
    }

    /**
     * Update a layer in the layers list
     */
    fun updateLayer(layer: Layer) {
        val index = layers.indexOfFirst { it.id == layer.id }
        if (index >= 0) {
            layers[index] = layer
        }
    }
}
