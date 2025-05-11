package com.example.imagemanipulator.shared.drawing

import com.example.imagemanipulator.shared.model.Layer

/**
 * Platform-agnostic canvas interface that each platform will implement
 */
interface PlatformCanvas {
    /**
     * Draw a layer on the canvas
     */
    fun drawLayer(layer: Layer, x: Float, y: Float, width: Float, height: Float)

    /**
     * Draw a layer with transformations
     */
    fun drawLayerWithTransform(
        layer: Layer,
        x: Float,
        y: Float,
        width: Float,
        height: Float,
        rotation: Float,
        scaleX: Float,
        scaleY: Float
    )

    /**
     * Clear the canvas
     */
    fun clear()
}