package com.example.imagemanipulator.shared.ui.layers

import androidx.compose.runtime.Composable
import com.example.imagemanipulator.shared.LayerViewModel

/**
 * Layer controls view that will be implemented per platform
 */
@Composable
expect fun LayerControlsView(layerViewModel: LayerViewModel)