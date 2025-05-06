package com.example.imagemanipulator.android.ui.canvas

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import io.github.markyav.drawbox.drawbox.DrawBox

@Composable
fun CanvasView(viewModel: com.example.imagemanipulator.android.viewmodel.CanvasViewModel) {
    DrawBox(modifier = Modifier.fillMaxSize())
}
build