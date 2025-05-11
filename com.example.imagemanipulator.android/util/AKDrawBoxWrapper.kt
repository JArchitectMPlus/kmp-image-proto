package com.example.imagemanipulator.android.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.MutableRect
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.asComposePaint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import com.example.imagemanipulator.shared.model.ImageLayer
import io.ak1.drawbox.DrawBox
import io.ak1.drawbox.DrawController
import io.ak1.drawbox.rememberDrawController

private const val TAG = "AKDrawBoxWrapper"
@Composable
fun AKDrawBoxWrapper(
    bitmap: Bitmap,
    onDraw: (Canvas) -> Unit,
    modifier: Modifier = Modifier,
    x: Float = 0f,
    y: Float = 0f,
    rotation: Float = 0f,
    scale: Float = 1f,
    onPositionChange: ((Float, Float) -> Unit)? = null,
) {
    var startPosition by remember { mutableStateOf(Offset.Zero) }
    var currentPosition by remember { mutableStateOf(Offset(x, y)) }
    var isDragging by remember { mutableStateOf(false) }

    // Update the position when x or y changes
    LaunchedEffect(x, y) {
        currentPosition = Offset(x, y)
    }

    androidx.compose.foundation.Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onTap = { offset ->
                    startPosition = offset
                })
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        startPosition = offset
                        isDragging = true
                    },
                    onDragEnd = {
                        isDragging = false
                    },
                    onDragCancel = {
                        isDragging = false
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()

                        // Update position based on drag
                        currentPosition = Offset(
                            currentPosition.x + dragAmount.x,
                            currentPosition.y + dragAmount.y
                        )

                        // Notify the caller about position change
                        onPositionChange?.invoke(currentPosition.x, currentPosition.y)

                        Log.d(TAG, "Drag updated, new position: $currentPosition")
                    }
                )
            }
            .onGloballyPositioned { coordinates ->
                Log.d(TAG, "Canvas bounds in parent: ${coordinates.boundsInParent()}")
                Log.d(TAG, "Canvas bounds in root: ${coordinates.boundsInRoot()}")
            }
    ) {
        drawIntoCanvas { canvas ->
            val nativeCanvas = canvas.nativeCanvas
            val paint = Paint().apply {
                isAntiAlias = true
            }

            // Save canvas state before transformations
            nativeCanvas.save()

            // Apply transformations in the right order
            nativeCanvas.translate(currentPosition.x, currentPosition.y)
            nativeCanvas.rotate(rotation)
            nativeCanvas.scale(scale, scale)

            // Draw the image at the origin (0,0) since we've already translated
            nativeCanvas.drawBitmap(bitmap, 0f, 0f, paint)

            // Execute any additional drawing operations
            val androidCanvas = Canvas(bitmap)
            onDraw(androidCanvas)

            // Restore canvas state
            nativeCanvas.restore()
        }
    }
}

fun drawLayer(canvas: Canvas, layer: ImageLayer) {
    val paint = Paint()
    // Apply transformations
    canvas.save()
    canvas.translate(layer.x, layer.y)
    canvas.rotate(layer.rotation)
    canvas.scale(layer.scale, layer.scale)

    // Draw the image
    val layerImage = layer.image
    if (layerImage is android.graphics.Bitmap) {
        canvas.drawBitmap(layerImage, 0f, 0f, paint)
    }

    canvas.restore()
}

// Use the original DrawBox from io.ak1.drawbox package directly