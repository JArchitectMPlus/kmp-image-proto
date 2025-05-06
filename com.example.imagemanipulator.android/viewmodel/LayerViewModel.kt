package com.example.imagemanipulator.android.viewmodel

import androidx.lifecycle.ViewModel
import com.example.imagemanipulator.shared.model.Layer

class LayerViewModel(layers: MutableList<Layer>) : ViewModel() {
    val layers: MutableList<Layer> = layers
}