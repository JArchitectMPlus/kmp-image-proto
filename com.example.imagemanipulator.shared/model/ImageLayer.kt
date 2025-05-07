package com.example.imagemanipulator.shared.model

import com.example.imagemanipulator.shared.util.KorIMWrapper

class ImageLayer(
    val path: String, 
    override val id: String, 
    var image: Any? = null, 
    var scale: Float = 1f, 
    var x: Float = 0f, 
    var y: Float = 0f, 
    var rotation: Float = 0f
) : Layer {

    override fun draw() {
        // Platform-specific drawing will be implemented
    }

    suspend fun loadImage(korIMWrapper: KorIMWrapper) {
        this.image = korIMWrapper.load(path)
    }
}