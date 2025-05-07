package com.example.imagemanipulator.shared.util

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import io.ak1.drawbox.DrawBox
import io.ak1.drawbox.rememberDrawController

@Composable
fun AKDrawBoxWrapper(
    modifier: Modifier = Modifier,
    paths: List<Path>,
    onDraw: (Path) -> Unit,
    clear: Boolean = false,
    undo: Boolean = false
) {
    val drawController = rememberDrawController()

    if (clear) {
        drawController.reset()
    }
    if (undo) {
        drawController.unDo()
    }

    Box(modifier = modifier) {
        DrawBox(
            drawController = drawController,
            modifier = Modifier.fillMaxSize()
                .background(Color.Transparent),
            onDraw = { drawInfo ->
                val newPath = Path()
                drawInfo.pathList.forEach { pathInfo ->
                  newPath.addPath(pathInfo.path)
                }
                onDraw(newPath)
            }
        )
        Canvas(modifier = Modifier.fillMaxSize()) {
            paths.forEach { path ->
                drawPath(
                    path = path,
                    color = Color.Black,
                    style = Stroke(width = 2f)
                )
            }
        }
    }
}