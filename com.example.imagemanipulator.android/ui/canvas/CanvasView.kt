package com.example.imagemanipulator.android.ui.canvas

import android.graphics.Canvas
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import com.example.imagemanipulator.android.drawing.AndroidPlatformCanvas
import com.example.imagemanipulator.android.drawing.AndroidPlatformDrawBox
import com.example.imagemanipulator.shared.CanvasViewModel
import com.example.imagemanipulator.shared.model.Layer
import com.example.imagemanipulator.shared.transformation.TransformationManager

@Composable
fun CanvasView(canvasViewModel: CanvasViewModel) {
    val layers by canvasViewModel.layers.collectAsState()
    val transformationManager by canvasViewModel.transformationManager.collectAsState()
    val canvas by canvasViewModel.canvas.collectAsState()

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        transformationManager.onDragStart(
                            offset.toLayerPoint(
                                canvas.width.toFloat(),
                                canvas.height.toFloat()
                            )
                        )
                    },
                    onDrag = { change, dragAmount ->
                        transformationManager.onDrag(
                            change.position.toLayerPoint(
                                canvas.width.toFloat(),
                                canvas.height.toFloat()
                            )
                        )
                    },
                    onDragEnd = { transformationManager.onDragEnd() },
                    onDragCancel = { transformationManager.onDragEnd() },
                )
            }
    ) {
        drawIntoCanvas { canvasCompose ->
            val nativeCanvas: Canvas = canvasCompose.nativeCanvas
            val platformCanvas = AndroidPlatformCanvas()
            platformCanvas.setCanvas(nativeCanvas)

            AndroidPlatformDrawBox().drawOnCanvas {
                 for (layer in layers) {
                     drawLayer(it, layer)
                 }
            }
        }
    }
}

private fun Offset.toLayerPoint(canvasWidth: Float, canvasHeight: Float) =
    com.example.imagemanipulator.shared.util.Point(
        x = x / canvasWidth,
        y = y / canvasHeight,
    )

private fun drawLayer(canvas: PlatformCanvas, layer: Layer) {
    when (layer) {
        is Layer.Image -> {
            println("Drawing image layer")
        }
        is Layer.Text -> {
            println("Drawing text layer")
        }
    }
}