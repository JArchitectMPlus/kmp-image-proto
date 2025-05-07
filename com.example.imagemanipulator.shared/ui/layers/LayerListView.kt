package com.example.imagemanipulator.android.ui.layers

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.imagemanipulator.shared.model.TextLayer
import com.example.imagemanipulator.shared.LayerViewModel

@Composable
fun LayerListView(viewModel: LayerViewModel, onSelectImageClick: () -> Unit) {
    Text(text = "Layer List")
    Button(onClick = {
        onSelectImageClick()
    }) {
        Text("Select Image")
    }
    Button(onClick = {
        viewModel.addLayer(TextLayer(id = "newTextLayer", text = "New Text", color = "#000000", font = "Arial", curve = 0f))
    }) {
        Text("Add Text Layer")
    }
}
