package com.example.imagemanipulator.shared

import com.example.imagemanipulator.shared.model.ImageLayer
import com.example.imagemanipulator.shared.model.Layer
import com.example.imagemanipulator.shared.transformation.PositionTransformation
import com.example.imagemanipulator.shared.transformation.RotationTransformation
import com.example.imagemanipulator.shared.transformation.ScaleTransformation
import com.example.imagemanipulator.shared.transformation.TransformationManager
import com.example.imagemanipulator.shared.transformation.TransformationSystem
import com.example.imagemanipulator.shared.transformation.ImageTransformation
import com.example.imagemanipulator.shared.transformation.Point
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Shared view model for image manipulation that can be used by both iOS and Android platforms.
 * This class uses Kotlin's MutableStateFlow for state management.
 */
open class ImageViewModel {
    // Create a transformation system to manage all transformations
    private val transformationSystem = TransformationSystem()

    // Keep the legacy transformation object for backward compatibility
    private val _transformation = ImageTransformation()
    val transformation: ImageTransformation = _transformation

    // Observable states for transformations
    private val _scale = MutableStateFlow(_transformation.scale)
    val scale: StateFlow<Float> = _scale.asStateFlow()

    private val _rotation = MutableStateFlow(_transformation.rotation)
    val rotation: StateFlow<Float> = _rotation.asStateFlow()

    private val _offset = MutableStateFlow(_transformation.offset)
    val offset: StateFlow<Point> = _offset.asStateFlow()

    // Layer management
    private val _layers = MutableStateFlow<List<Layer>>(emptyList())
    val layers: StateFlow<List<Layer>> = _layers.asStateFlow()

    private val _selectedLayerId = MutableStateFlow<String?>(null)
    val selectedLayerId: StateFlow<String?> = _selectedLayerId.asStateFlow()

    /**
     * Reset all transformations
     */
    fun resetTransformations() {
        _transformation.reset()
        transformationSystem.clearTransformations()
        updateStates()
    }

    /**
     * Apply scale and update state
     */
    fun applyScale(scaleFactor: Float) {
        _transformation.applyScale(scaleFactor)

        // Also apply to selected layer if any
        getSelectedLayer()?.let { layer ->
            if (layer is ImageLayer) {
                transformationSystem.addTransformation(ScaleTransformation(layer.scale * scaleFactor))
                layer.applyTransformations()
            }
        }

        updateStates()
    }

    /**
     * Set scale directly and update state
     */
    fun setScale(newScale: Float) {
        _transformation.setScale(newScale)

        // Also apply to selected layer if any
        getSelectedLayer()?.let { layer ->
            if (layer is ImageLayer) {
                transformationSystem.addTransformation(ScaleTransformation(newScale))
                layer.applyTransformations()
            }
        }

        updateStates()
    }

    /**
     * Apply rotation and update state
     */
    fun applyRotation(degrees: Float) {
        _transformation.applyRotation(degrees)

        // Also apply to selected layer if any
        getSelectedLayer()?.let { layer ->
            if (layer is ImageLayer) {
                transformationSystem.addTransformation(RotationTransformation(layer.rotation + degrees))
                layer.applyTransformations()
            }
        }

        updateStates()
    }

    /**
     * Set rotation directly and update state
     */
    fun setRotation(degrees: Float) {
        _transformation.setRotation(degrees)

        // Also apply to selected layer if any
        getSelectedLayer()?.let { layer ->
            if (layer is ImageLayer) {
                transformationSystem.addTransformation(RotationTransformation(degrees))
                layer.applyTransformations()
            }
        }

        updateStates()
    }

    /**
     * Apply translation and update state
     */
    fun applyTranslation(dx: Float, dy: Float) {
        _transformation.applyTranslation(dx, dy)

        // Also apply to selected layer if any
        getSelectedLayer()?.let { layer ->
            if (layer is ImageLayer) {
                transformationSystem.addTransformation(PositionTransformation(layer.x + dx, layer.y + dy))
                layer.applyTransformations()
            }
        }

        updateStates()
    }

    /**
     * Add a layer to the canvas
     */
    fun addLayer(layer: Layer) {
        val currentLayers = _layers.value.toMutableList()
        currentLayers.add(layer)
        _layers.value = currentLayers

        // Select the newly added layer
        selectLayer(layer.id)
    }

    /**
     * Remove a layer from the canvas
     */
    fun removeLayer(layerId: String) {
        val currentLayers = _layers.value.toMutableList()
        val layerToRemove = currentLayers.find { it.id == layerId }

        layerToRemove?.let {
            currentLayers.remove(it)
            _layers.value = currentLayers

            // If we removed the selected layer, deselect it
            if (_selectedLayerId.value == layerId) {
                _selectedLayerId.value = null
            }
        }
    }

    /**
     * Select a layer by ID
     */
    fun selectLayer(layerId: String) {
        if (_layers.value.any { it.id == layerId }) {
            _selectedLayerId.value = layerId
        }
    }

    /**
     * Deselect the current layer
     */
    fun deselectLayer() {
        _selectedLayerId.value = null
    }

    /**
     * Get the currently selected layer
     */
    fun getSelectedLayer(): Layer? {
        val selectedId = _selectedLayerId.value ?: return null
        return _layers.value.find { it.id == selectedId }
    }

    /**
     * Apply a transformation to a specific layer
     */
    fun applyTransformationToLayer(layerId: String, transformation: TransformationManager) {
        val layer = _layers.value.find { it.id == layerId }
        layer?.let {
            transformationSystem.addTransformation(transformation)
            it.applyTransformations()
        }
    }

    /**
     * Update all state flows with the current transformation values
     */
    private fun updateStates() {
        _scale.value = _transformation.scale
        _rotation.value = _transformation.rotation
        _offset.value = _transformation.offset
    }
}