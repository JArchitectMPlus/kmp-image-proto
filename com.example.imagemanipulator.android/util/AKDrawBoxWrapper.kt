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
    drawController: DrawController = rememberDrawController(),
) {

    DrawBox(
        drawController = drawController,
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onTap = { offset ->
                    start = offset
                })
            }
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    Log.d(TAG, "Drag detected: ${change.position}, $dragAmount")
                }
            }
    ) {
        Canvas(
            modifier = Modifier.fillMaxSize().onGloballyPositioned { coordinates ->
                Log.d(TAG, "Canvas bounds in parent: ${coordinates.boundsInParent()}")
                Log.d(TAG, "Canvas bounds in root: ${coordinates.boundsInRoot()}")
            }
        ) {
            val imageBitmap = BitmapFactory.decodeResource(
                context.resources,
                com.example.imagemanipulator.android.R.drawable.test_image
            )
            val imageLayer = ImageLayer(
                id = 1,
                image = imageBitmap,
                position = Offset(100f, 100f), // Example position
                rotation = 45f, // Example rotation
                scale = 0.5f // Example scale
            )



            drawIntoCanvas { canvas ->
                canvas.nativeCanvas.drawBitmap(bitmap, 0f, 0f, Paint())
                val androidCanvas = Canvas(bitmap)
                onDraw(androidCanvas)
            }
        }
    }
}

fun drawLayer(canvas: Canvas, layer: ImageLayer) {
    val paint = Paint()
    // Apply transformations
    canvas.save()
    canvas.translate(layer.position.x, layer.position.y)
    canvas.rotate(layer.rotation)
    canvas.scale(layer.scale, layer.scale)

    // Draw the image
    canvas.drawBitmap(layer.image, 0f, 0f, paint)

    canvas.restore()
}

@Composable
fun DrawBox(
    drawController: DrawController = rememberDrawController(),
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    io.ak1.drawbox.DrawBox(
        drawController = drawController,
        modifier = modifier,
        content = content
    )
}