package com.example.imagemanipulator.shared.ui.layers

import androidx.compose.runtime.Composable
import com.example.imagemanipulator.shared.LayerViewModel

/**
 * iOS implementation of the layer controls view.
 * This implementation satisfies the expect/actual pattern without changing the Android implementation.
 */
@Composable
actual fun LayerControlsView(layerViewModel: LayerViewModel) {
    // This is a minimal implementation to satisfy the contract
    // In a real app, this would create proper iOS UI controls
    // For now, we defer all functionality to the shared LayerViewModel
}