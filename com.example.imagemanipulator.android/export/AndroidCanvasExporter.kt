package com.example.imagemanipulator.android.export

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.Base64
import com.example.imagemanipulator.shared.export.CanvasExporter
import com.example.imagemanipulator.shared.model.Canvas as ModelCanvas
import com.example.imagemanipulator.shared.model.ImageLayer
import com.example.imagemanipulator.shared.model.Layer
import com.example.imagemanipulator.shared.model.TextLayer
import java.io.ByteArrayOutputStream

/**
 * Android-specific implementation of Canvas exporter
 */
class AndroidCanvasExporter : CanvasExporter() {

    /**
     * Exports the canvas with all its layers to a Base64 encoded string.
     *
     * @param canvas The canvas model
     * @param layers The layers to include in the export
     * @return Base64 encoded string of the canvas content
     */
    override fun exportToBase64(canvas: ModelCanvas, layers: List<Layer>): String {
        // Create a bitmap to draw on
        val width = 1000  // Default width
        val height = 1000 // Default height
        
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val androidCanvas = Canvas(bitmap)
        
        // Fill with white background
        androidCanvas.drawColor(Color.WHITE)
        
        // Draw each visible layer
        val paint = Paint().apply {
            isAntiAlias = true
        }
        
        // Draw the layers in order (first to last)
        layers.forEach { layer ->
            if (layer.isVisible()) {
                when (layer) {
                    is ImageLayer -> {
                        val layerImage = layer.image as? Bitmap ?: return@forEach
                        
                        // Save the canvas state before applying transformations
                        androidCanvas.save()
                        
                        // Apply transformations
                        androidCanvas.translate(layer.x, layer.y)
                        androidCanvas.rotate(layer.rotation)
                        androidCanvas.scale(layer.scale, layer.scale)
                        
                        // Draw the bitmap at the origin (0,0) since we've translated
                        androidCanvas.drawBitmap(layerImage, 0f, 0f, paint)
                        
                        // Restore the canvas state
                        androidCanvas.restore()
                    }
                    is TextLayer -> {
                        // Save the canvas state
                        androidCanvas.save()
                        
                        // Set up text paint
                        val textPaint = Paint().apply {
                            isAntiAlias = true
                            textSize = 50f
                            try {
                                color = Color.parseColor(layer.color)
                            } catch (e: Exception) {
                                color = Color.BLACK
                            }
                        }
                        
                        // Apply transformations
                        androidCanvas.translate(layer.x, layer.y)
                        androidCanvas.rotate(layer.rotation)
                        androidCanvas.scale(layer.scale, layer.scale)
                        
                        // Draw the text at the origin
                        androidCanvas.drawText(layer.text, 0f, 0f, textPaint)
                        
                        // Restore the canvas state
                        androidCanvas.restore()
                    }
                }
            }
        }
        
        // Convert bitmap to base64
        return bitmapToBase64(bitmap)
    }
    
    /**
     * Converts a bitmap to a base64 encoded string
     */
    private fun bitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()

        // Compress the bitmap to PNG format
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)

        val byteArray = byteArrayOutputStream.toByteArray()
        return convertBytesToBase64(byteArray)
    }

    /**
     * Override the base64 conversion methods
     */
    override fun convertBytesToBase64(bytes: ByteArray): String {
        return Base64.encodeToString(bytes, Base64.DEFAULT)
    }

    override fun convertBase64ToBytes(base64: String): ByteArray {
        return Base64.decode(base64, Base64.DEFAULT)
    }
}