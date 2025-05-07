package com.example.imagemanipulator.shared.util

import com.example.imagemanipulator.shared.model.Layer

expect class PlatformDrawBox() {
    fun drawOnCanvas(draw: (PlatformCanvas) -> Unit)
}

expect class PlatformCanvas() {
    fun drawRect(x: Float, y: Float, width: Float, height: Float, color: Int)
    fun drawLine(startX: Float, startY: Float, endX: Float, endY: Float, color: Int)
    fun drawCircle(centerX: Float, centerY: Float, radius: Float, color: Int)
    fun drawText(text: String, x: Float, y: Float, color: Int, fontSize: Float)
    fun drawImage(image: Any, x: Float, y: Float, width: Float, height: Float)
    fun drawLayer(layer: Layer, x: Float, y: Float, width: Float, height: Float)
}