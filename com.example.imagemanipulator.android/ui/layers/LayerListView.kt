package com.example.imagemanipulator.android.ui.layers

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.imagemanipulator.android.viewmodel.LayerViewModel
import com.example.imagemanipulator.shared.model.TextLayer

@Composable
fun LayerListView(viewModel: LayerViewModel) {
    Text(text = "Layer List")
    Button(onClick = {
        viewModel.addLayer(TextLayer(id = "newTextLayer", text = "New Text", color = "#000000", font = "Arial", curve = 0f))
    }) {
        Text("Add Text Layer")
    }
}
