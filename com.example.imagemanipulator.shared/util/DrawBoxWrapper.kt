package com.example.imagemanipulator.shared.util

import com.example.imagemanipulator.shared.model.Layer

class DrawBoxWrapper {
    fun drawLayer(layer: Layer){
        println("drawing layer: ${layer.id}")
    }
}
