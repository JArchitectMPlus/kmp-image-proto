package com.example.imagemanipulator.android.ui.layers

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.imagemanipulator.android.viewmodel.LayerViewModel
import com.example.imagemanipulator.shared.LayerViewModel

@Composable
fun LayerControlsView(layerViewModel: LayerViewModel) {
    Text(text = "Layer Controls")
}