package com.example.imagemanipulator.ios.drawing

import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreGraphics.CGContextRef
import platform.UIKit.UIGraphicsGetCurrentContext

/**
 * iOS helper class for drawing on a Canvas
 */
@ExperimentalForeignApi
class IOSPlatformDrawBox {
    /**
     * Draw on the canvas using the provided callback
     */
    @ExperimentalForeignApi
    fun drawOnCanvas(canvas: IOSPlatformCanvas, callback: (IOSPlatformCanvas) -> Unit) {
        // Get the current graphics context
        val context = UIGraphicsGetCurrentContext() ?: return

        // Set the context on the canvas
        canvas.setContext(context)

        // Call the drawing callback
        callback(canvas)
    }
}