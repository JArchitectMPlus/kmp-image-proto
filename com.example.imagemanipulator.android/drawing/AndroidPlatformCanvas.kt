package com.example.imagemanipulator.shared.drawing

import android.graphics.Canvas

actual class AndroidPlatformCanvas : PlatformCanvas() {
    var canvas: Canvas? = null
        private set

    fun setCanvas(canvas: Canvas) {
        this.canvas = canvas
    }
}