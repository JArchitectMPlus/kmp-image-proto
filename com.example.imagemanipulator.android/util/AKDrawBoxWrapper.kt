package com.example.imagemanipulator.android.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.asComposePaint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import io.ak1.drawbox.DrawBox
import io.ak1.drawbox.DrawController
import io.ak1.drawbox.rememberDrawController

@Composable
fun AKDrawBoxWrapper(
    bitmap: Bitmap,
    onDraw: (Canvas) -> Unit,
    modifier: Modifier = Modifier,
    drawController: DrawController = rememberDrawController(),
) {
    var start by remember { mutableStateOf(Offset.Zero) }
    var end by remember { mutableStateOf(Offset.Zero) }
    DrawBox(
        drawController = drawController,
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onTap = { offset ->
                    start = offset
                    end = offset
                })
            }
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    start = change.position
                    end = change.position
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawIntoCanvas { canvas ->
                canvas.nativeCanvas.drawBitmap(bitmap, 0f, 0f, Paint())
                val androidCanvas = Canvas(bitmap)
                onDraw(androidCanvas)
            }
        }
    }
}