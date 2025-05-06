package com.example.imagemanipulator.shared.export

class CanvasExporter {
    fun export(canvas: com.example.imagemanipulator.shared.model.Canvas): String {
        for (layer in canvas.layers) {
            layer.draw()
        }
        return ""
    }
}
