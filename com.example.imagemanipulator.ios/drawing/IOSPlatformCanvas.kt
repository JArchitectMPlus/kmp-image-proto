package com.example.imagemanipulator.ios.drawing

import com.example.imagemanipulator.shared.model.Layer
import com.example.imagemanipulator.shared.drawing.PlatformCanvas
import platform.CoreGraphics.CGPoint
import platform.CoreGraphics.CGRect
import platform.CoreGraphics.CGContextFillRect
import platform.CoreGraphics.CGContextRef
import platform.CoreGraphics.CGContextRef

actual class IOSPlatformCanvas : PlatformCanvas() {
    var context: CGContextRef? = null
        private set

    fun setContext(context: CGContextRef) {
        this.context = context
    }

    fun getContext(): CGContextRef? {
        return context
    }

    fun drawRect(x: Double, y: Double, width: Double, height: Double) {
        context?.let {
            CGContextFillRect(it, CGRect(x, y, width, height))
        }
    }

    fun drawLine(x1: Double, y1: Double, x2: Double, y2: Double) {
        // TODO: Implement line drawing using CGContext
    }

    fun drawCircle(x: Double, y: Double, radius: Double) {
        // TODO: Implement circle drawing using CGContext
    }

    fun drawText(text: String, x: Double, y: Double) {
        // TODO: Implement text drawing using CGContext
    }

    fun drawImage(image: Any, x: Double, y: Double, width: Double, height: Double) {
        // TODO: Implement image drawing using CGContext
    }

    fun drawLayer(layer: Layer, x: Double, y: Double, width: Double, height: Double) {
        // TODO: Implement layer drawing based on layer type (ImageLayer or TextLayer)
    }

}