package com.example.imagemanipulator.android.viewmodel

import androidx.lifecycle.ViewModel
import com.example.imagemanipulator.shared.model.Layer

class LayerViewModel(layers: MutableList<Layer>) : ViewModel() {
    val layers: MutableList<Layer> = layers

    fun addLayer(layer: Layer) {
        layers.add(layer)
    }

    fun removeLayer(layer: Layer) {
        layers.remove(layer)
    }

    fun duplicateLayer(layer: Layer) {
        // For now, creating a simple copy. This should be a deep copy in a real application
        layers.add(layer)
    }
}