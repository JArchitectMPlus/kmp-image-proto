package com.example.imagemanipulator.android.viewmodel

import androidx.lifecycle.ViewModel
import com.example.imagemanipulator.shared.model.Layer
import com.example.imagemanipulator.shared.model.Canvas
import com.example.imagemanipulator.shared.transformation.TransformationManager

class CanvasViewModel(val canvas: Canvas) : ViewModel()
 {
    fun addLayer(layer: Layer) {
        canvas.addLayer(layer)
    }
    fun applyTransformation(transformation: TransformationManager, layer: Layer) {
        transformation.apply(layer)
    }
}