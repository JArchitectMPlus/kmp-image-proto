package com.example.imagemanipulator.ios.drawing

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import com.example.imagemanipulator.shared.drawing.PlatformCanvas
import com.example.imagemanipulator.shared.model.ImageLayer
import com.example.imagemanipulator.shared.model.Layer
import com.example.imagemanipulator.shared.model.TextLayer
import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreGraphics.CGContextRef
import platform.CoreGraphics.CGContextSaveGState
import platform.CoreGraphics.CGContextRestoreGState
import platform.CoreGraphics.CGContextTranslateCTM
import platform.CoreGraphics.CGContextRotateCTM
import platform.CoreGraphics.CGContextScaleCTM
import platform.CoreGraphics.CGRectMake
import platform.UIKit.UIImage
import platform.UIKit.UIView
import kotlin.math.PI

/**
 * iOS implementation of the PlatformCanvas
 */
@ExperimentalForeignApi
class IOSPlatformCanvas : PlatformCanvas {
    private var context: CGContextRef? = null

    /**
     * Set the context to draw on
     */
    @ExperimentalForeignApi
    fun setContext(context: CGContextRef) {
        this.context = context
    }

    @ExperimentalForeignApi
    fun getContext(): CGContextRef? {
        return context
    }

    /**
     * Overloaded method to draw a layer with position and size
     */
    @ExperimentalForeignApi
    fun drawLayer(layer: Layer, position: Offset, size: IntSize) {
        drawLayer(layer, position.x, position.y, size.width.toFloat(), size.height.toFloat())
    }

    /**
     * Draw a layer on the canvas
     */
    @ExperimentalForeignApi
    override fun drawLayer(layer: Layer, x: Float, y: Float, width: Float, height: Float) {
        if (layer is ImageLayer) {
            val image = layer.image as? UIImage ?: return
            val rect = CGRectMake(x.toDouble(), y.toDouble(), width.toDouble(), height.toDouble())
            context?.let {
                // Save the current context state
                CGContextSaveGState(it)

                // Draw the image upside down because Core Graphics has a different coordinate system origin
                CGContextTranslateCTM(it, 0.0, height.toDouble())
                CGContextScaleCTM(it, 1.0, -1.0)

                image.drawInRect(rect)

                // Restore the context state
                CGContextRestoreGState(it)
            }
        }
    }

    /**
     * Draw a layer with transformations
     */
    @ExperimentalForeignApi
    override fun drawLayerWithTransform(
        layer: Layer,
        x: Float,
        y: Float,
        width: Float,
        height: Float,
        rotation: Float,
        scaleX: Float,
        scaleY: Float
    ) {
        if (layer is ImageLayer) {
            val image = layer.image as? UIImage ?: return
            val rect = CGRectMake(0.0, 0.0, width.toDouble(), height.toDouble())

            context?.let {
                // Save the current context state
                CGContextSaveGState(it)

                // Apply transformations in reverse order: translate, rotate, scale
                CGContextTranslateCTM(it, x.toDouble(), y.toDouble())
                CGContextTranslateCTM(it, (width / 2).toDouble(), (height / 2).toDouble())
                CGContextRotateCTM(it, rotation * PI / 180)
                CGContextScaleCTM(it, scaleX.toDouble(), scaleY.toDouble())
                CGContextTranslateCTM(it, -(width / 2).toDouble(), -(height / 2).toDouble())

                // Draw the image with flipped Y axis for Core Graphics coordinate system
                CGContextTranslateCTM(it, 0.0, height.toDouble())
                CGContextScaleCTM(it, 1.0, -1.0)

                image.drawInRect(rect)

                // Restore the context state
                CGContextRestoreGState(it)
            }
        }
    }

    /**
     * Clear the canvas
     */
    override fun clear() {
        // In iOS, clearing is typically handled by the view's drawing cycle
        // and doesn't need to be manually implemented here
    }
}