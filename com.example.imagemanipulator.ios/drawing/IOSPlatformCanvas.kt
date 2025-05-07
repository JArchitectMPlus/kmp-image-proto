package com.example.imagemanipulator.ios.drawing

import com.example.imagemanipulator.shared.model.Layer
import com.example.imagemanipulator.shared.model.ImageLayer
import com.example.imagemanipulator.shared.drawing.PlatformCanvas
import platform.CoreGraphics.CGPoint
import platform.CoreGraphics.CGRect
import platform.CoreGraphics.CGContextFillRect
import platform.CoreGraphics.CGContextRef
import platform.CoreGraphics.CGContextRef
import platform.UIKit.UIImage
import platform.CoreGraphics.CGContextTranslateCTM
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

    fun drawLayer(layer: Layer, x: Double, y: Double, width: Double, height: Double, angleDegrees: Double, scaleX: Double, scaleY: Double) {
        context?.let {
            when (layer) {
                is ImageLayer -> {
                    val image = UIImage.imageNamed("testImage")
                    image?.let { uiImage ->
                        val rect = CGRect(x, y, width, height)

                        // Save the current context state
                        platform.CoreGraphics.CGContextSaveGState(it)

                        // Apply transformations in reverse order: translate, rotate, scale
                        platform.CoreGraphics.CGContextTranslateCTM(it, x + width / 2, y + height / 2)
                        platform.CoreGraphics.CGContextRotateCTM(it, angleDegrees * kotlin.math.PI / 180)
                        platform.CoreGraphics.CGContextScaleCTM(it, scaleX, scaleY)
                        platform.CoreGraphics.CGContextTranslateCTM(it, -(x + width / 2), -(y + height / 2))

                        // Draw the image upside down because Core Graphics has a different coordinate system origin
                        platform.CoreGraphics.CGContextTranslateCTM(it, 0.0, height)
                        platform.CoreGraphics.CGContextScaleCTM(it, 1.0, -1.0)

                        uiImage.drawInRect(rect)

                        // Restore the context state
                        platform.CoreGraphics.CGContextRestoreGState(it)
                    }
                }
                // TODO: Implement TextLayer drawing
                else -> {}
            }
        }
    }
}