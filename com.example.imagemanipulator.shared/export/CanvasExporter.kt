package com.example.imagemanipulator.shared.export

import com.example.imagemanipulator.shared.model.Canvas
import com.example.imagemanipulator.shared.model.Layer

/**
 * Utility class for exporting canvas content in various formats
 */
open class CanvasExporter {
    /**
     * Basic export function that draws all layers
     */
    fun export(canvas: Canvas): String {
        for (layer in canvas.layers) {
            layer.draw()
        }
        return ""
    }

    /**
     * Exports the canvas with all its layers to a Base64 encoded string.
     * The actual implementation depends on the platform.
     *
     * @param canvas The canvas to export
     * @param layers The layers to include in the export
     * @return Base64 encoded string of the canvas content
     */
    open fun exportToBase64(canvas: Canvas, layers: List<Layer>): String {
        // Platform-specific implementation will override this
        return ""
    }

    /**
     * Helper function to convert a byte array to a base64 string
     * This will be implemented by platform-specific classes
     */
    open fun convertBytesToBase64(bytes: ByteArray): String {
        // Platform-specific implementation will override this
        return ""
    }

    /**
     * Helper function to convert a base64 string back to a byte array
     * This will be implemented by platform-specific classes
     */
    open fun convertBase64ToBytes(base64: String): ByteArray {
        // Platform-specific implementation will override this
        return ByteArray(0)
    }
}
