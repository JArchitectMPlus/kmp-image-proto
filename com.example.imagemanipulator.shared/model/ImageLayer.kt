package com.example.imagemanipulator.shared.model

import com.example.imagemanipulator.shared.util.KorIMWrapper
import com.soywiz.korim.bitmap.Bitmap32

class ImageLayer(val path: String, override val id: String, var image: Bitmap32? = null, var scale: Float = 1f, var x: Float = 0f, var y: Float = 0f, var rotation: Float = 0f) : Layer {

    override fun draw() {
    }

    fun loadImage(korIMWrapper: KorIMWrapper) {
 this.image = korIMWrapper.load(path)
    }
}