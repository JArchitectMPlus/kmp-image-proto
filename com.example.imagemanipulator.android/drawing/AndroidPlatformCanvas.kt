package com.example.imagemanipulator.android.drawing

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import com.example.imagemanipulator.shared.drawing.PlatformCanvas
import com.example.imagemanipulator.shared.model.ImageLayer
import com.example.imagemanipulator.shared.model.Layer

/**
 * Android implementation of the PlatformCanvas
 */
class AndroidPlatformCanvas : PlatformCanvas {
    internal var canvas: Canvas? = null
    private val paint = Paint().apply {
        isAntiAlias = true
    }
    
    /**
     * Set the canvas to draw on
     */
    fun setCanvas(canvas: Canvas) {
        this.canvas = canvas
    }
    
    /**
     * Draw a layer on the canvas
     */
    override fun drawLayer(layer: Layer, x: Float, y: Float, width: Float, height: Float) {
        if (layer is ImageLayer) {
            val bitmap = layer.image as? Bitmap ?: return
            canvas?.drawBitmap(bitmap, x, y, paint)
        }
    }
    
    /**
     * Draw a layer with transformations
     */
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
            val bitmap = layer.image as? Bitmap ?: return
            canvas?.let { canvas ->
                // Save the current state
                val saveCount = canvas.save()
                
                // Apply transformations
                val matrix = Matrix()
                
                // Move to the center of where we want to draw the image
                matrix.postTranslate(x, y)
                
                // Apply rotation around the center point
                matrix.postRotate(
                    rotation,
                    x + (width / 2),
                    y + (height / 2)
                )
                
                // Apply scaling
                matrix.postScale(
                    scaleX,
                    scaleY,
                    x + (width / 2),
                    y + (height / 2)
                )
                
                // Draw the bitmap using the matrix
                canvas.concat(matrix)
                canvas.drawBitmap(bitmap, 0f, 0f, paint)
                
                // Restore the canvas state
                canvas.restoreToCount(saveCount)
            }
        }
    }
    
    /**
     * Clear the canvas
     */
    override fun clear() {
        canvas?.drawColor(android.graphics.Color.TRANSPARENT, android.graphics.PorterDuff.Mode.CLEAR)
    }
}