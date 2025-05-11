package com.example.imagemanipulator.shared

import com.example.imagemanipulator.shared.model.Canvas
import com.example.imagemanipulator.shared.model.Layer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CanvasViewModel {
    private val _canvas = MutableStateFlow(Canvas())
    val canvas: StateFlow<Canvas> = _canvas.asStateFlow()

    // Create empty list of layers
    private val _layers = MutableStateFlow<List<Layer>>(emptyList())
    val layers: StateFlow<List<Layer>> = _layers.asStateFlow()

    fun addLayer(layer: Layer) {
        // Update the flow
        val currentLayers = _layers.value.toMutableList()
        currentLayers.add(layer)
        _layers.value = currentLayers

        // Also update the canvas model
        _canvas.value.addLayer(layer)
    }

    fun removeLayer(layerId: String) {
        // Update the flow
        val currentLayers = _layers.value.toMutableList()
        currentLayers.removeAll { it.id == layerId }
        _layers.value = currentLayers

        // Also update the canvas model
        _canvas.value.removeLayerById(layerId)
    }

    fun clear() {
        // Update the flow
        _layers.value = emptyList()

        // Also update the canvas model
        _canvas.value.clear()
    }

    /**
     * Update a layer in the layers list and the canvas model
     */
    fun updateLayer(layer: Layer) {
        // Create a new canvas to trigger state change
        val newCanvas = Canvas(
            width = _canvas.value.width,
            height = _canvas.value.height,
            aspectRatio = _canvas.value.aspectRatio
        )

        // Update the flow with a new list to ensure reactivity
        val currentLayers = _layers.value.toMutableList()
        val index = currentLayers.indexOfFirst { it.id == layer.id }
        if (index >= 0) {
            currentLayers[index] = layer
            _layers.value = currentLayers.toList()  // Create a new list reference

            // Re-add all layers to the new canvas
            currentLayers.forEach {
                newCanvas.addLayer(it)
            }

            // Update the canvas with a new instance
            _canvas.value = newCanvas
        }

        // Logging is handled by platform-specific code
    }
}