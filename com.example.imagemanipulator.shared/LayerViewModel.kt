package com.example.imagemanipulator.shared

import com.example.imagemanipulator.shared.model.ImageLayer
import com.example.imagemanipulator.shared.model.Layer
import com.example.imagemanipulator.shared.model.TextLayer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


class LayerViewModel {
    private val _layers = MutableStateFlow<List<Layer>>(emptyList())
    val layers: StateFlow<List<Layer>> = _layers.asStateFlow()

    // Track the currently selected layer
    private val _selectedLayer = MutableStateFlow<Layer?>(null)
    val selectedLayer: StateFlow<Layer?> = _selectedLayer.asStateFlow()

    fun addLayer(layer: Layer) {
        val currentLayers = _layers.value.toMutableList()
        currentLayers.add(layer)
        _layers.value = currentLayers

        // Select the newly added layer
        selectLayer(layer)
    }

    fun removeLayer(layer: Layer) {
        val currentLayers = _layers.value.toMutableList()
        currentLayers.remove(layer)
        _layers.value = currentLayers

        // If we removed the selected layer, clear the selection
        if (_selectedLayer.value == layer) {
            _selectedLayer.value = null
        }
    }

    fun duplicateLayer(layer: Layer) {
        // Create a proper copy based on layer type
        val newLayer = when (layer) {
            is ImageLayer -> layer.clone("duplicate_" + layer.id)
            is TextLayer -> layer.clone("duplicate_" + layer.id)
            else -> return // Unsupported layer type
        }
        addLayer(newLayer)
    }

    fun selectLayer(layer: Layer?) {
        _selectedLayer.value = layer
    }

    fun deleteSelectedLayer() {
        _selectedLayer.value?.let { layer ->
            removeLayer(layer)
        }
    }

    /**
     * Clear all layers
     */
    fun clear() {
        _layers.value = emptyList()
        _selectedLayer.value = null
    }

    // Methods to manipulate layer properties
    fun setRotation(rotation: Float) {
        when (val layer = _selectedLayer.value) {
            is ImageLayer -> {
                layer.rotation = rotation
                updateLayer(layer)
            }
            is TextLayer -> {
                layer.rotation = rotation
                updateLayer(layer)
            }
            else -> return
        }
    }

    fun setScale(scale: Float) {
        when (val layer = _selectedLayer.value) {
            is ImageLayer -> {
                layer.scale = scale
                updateLayer(layer)
            }
            is TextLayer -> {
                layer.scale = scale
                updateLayer(layer)
            }
            else -> return
        }
    }

    fun setPosition(x: Float, y: Float) {
        when (val layer = _selectedLayer.value) {
            is ImageLayer -> {
                layer.x = x
                layer.y = y
                updateLayer(layer)
            }
            is TextLayer -> {
                layer.x = x
                layer.y = y
                updateLayer(layer)
            }
            else -> return
        }
    }

    fun toggleVisibility() {
        when (val layer = _selectedLayer.value) {
            is ImageLayer -> {
                layer.setVisibility(!layer.isVisible())
                updateLayer(layer)
            }
            is TextLayer -> {
                layer.setVisibility(!layer.isVisible())
                updateLayer(layer)
            }
            else -> return
        }
    }

    /**
     * Updates a layer in the layers list
     * Can be called from outside when using shared transformation systems
     */
    fun updateLayer(layer: Layer) {
        // Create a new reference to the layer to ensure state update
        val updatedLayer = when (layer) {
            is ImageLayer -> ImageLayer(
                id = layer.id,
                path = layer.path,
                image = layer.image,
                scale = layer.scale,
                x = layer.x,
                y = layer.y,
                rotation = layer.rotation,
                isVisible = layer.isVisible()
            )
            is TextLayer -> TextLayer(
                id = layer.id,
                text = layer.text,
                color = layer.color,
                font = layer.font,
                curve = layer.curve,
                x = layer.x,
                y = layer.y,
                scale = layer.scale,
                rotation = layer.rotation,
                isVisible = layer.isVisible()
            )
            else -> layer
        }

        // Force a UI update by recreating the layers list
        val currentLayers = _layers.value.toMutableList()
        val index = currentLayers.indexOfFirst { it.id == layer.id }
        if (index >= 0) {
            currentLayers[index] = updatedLayer
            _layers.value = currentLayers.toList() // Create a new list reference to trigger State update

            // Also update the selected layer reference if this is the currently selected layer
            if (_selectedLayer.value?.id == layer.id) {
                _selectedLayer.value = updatedLayer
            }
        }

        // Logging is handled by platform-specific code
    }

    // Text layer specific methods
    fun setText(text: String) {
        val layer = _selectedLayer.value as? TextLayer ?: return
        val newLayer = TextLayer(
            id = layer.id,
            text = text,
            color = layer.color,
            font = layer.font,
            curve = layer.curve,
            x = layer.x,
            y = layer.y,
            scale = layer.scale,
            rotation = layer.rotation
        )
        updateLayer(newLayer)
    }

    fun setTextColor(color: String) {
        val layer = _selectedLayer.value as? TextLayer ?: return
        val newLayer = TextLayer(
            id = layer.id,
            text = layer.text,
            color = color,
            font = layer.font,
            curve = layer.curve,
            x = layer.x,
            y = layer.y,
            scale = layer.scale,
            rotation = layer.rotation
        )
        updateLayer(newLayer)
    }
}