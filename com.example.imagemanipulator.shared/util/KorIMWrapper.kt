package com.example.imagemanipulator.shared.util

class KorIMWrapper {
    fun load(path: String): Any? {
        println("loading image from: $path")
        return null
    }
    fun convert(image: Any, format: String): Any? {
        println("converting image to: $format")
        return null
    }
}
