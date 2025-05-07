package com.example.imagemanipulator.android.ui.canvas

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import com.example.imagemanipulator.shared.CanvasViewModel
import androidx.compose.ui.geometry.Offset
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import android.graphics.Paint

import com.example.imagemanipulator.shared.util.AKDrawBoxWrapper // Import the new wrapper
import com.example.imagemanipulator.shared.model.TextLayer
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import com.example.imagemanipulator.shared.model.ImageLayer


@Composable
fun CanvasView(viewModel: CanvasViewModel) {
    DrawBox(modifier = Modifier.fillMaxSize()) { drawInfo ->
        viewModel.canvas.layers.forEach { layer ->
            AKDrawBoxWrapper.drawOnCanvas(drawInfo.canvas) {
                when (layer) {
                    is ImageLayer -> if (layer.image != null) {
                        it.save()
                        it.translate(layer.x, layer.y)
                        it.rotate(layer.rotation)
                        it.scale(layer.scale, layer.scale)
                        it.drawImage(layer.image as ImageBitmap, Offset(0f, 0f))
                        it.restore()
                    }

                    is TextLayer -> {
                        val paint = Paint().apply {
                            color = android.graphics.Color.parseColor(layer.color)
                        }
                        it.nativeCanvas.drawText(layer.text, 100f, 100f, paint)
                    }
                }
            }
        }
    }
}