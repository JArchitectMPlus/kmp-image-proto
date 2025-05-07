package com.example.imagemanipulator.android.drawing

import android.graphics.Canvas
import com.example.imagemanipulator.android.util.AKDrawBoxWrapper
import com.example.imagemanipulator.shared.drawing.PlatformCanvas
import com.example.imagemanipulator.shared.drawing.PlatformDrawBox

actual class AndroidPlatformDrawBox : PlatformDrawBox {
    private var drawBoxWrapper: AKDrawBoxWrapper? = null

    override fun drawOnCanvas(platformCanvas: PlatformCanvas, draw: (PlatformCanvas) -> Unit) {
        if (platformCanvas is AndroidPlatformCanvas) {
            val canvas: Canvas? = platformCanvas.canvas
            if (canvas != null) {
                drawBoxWrapper = AKDrawBoxWrapper(canvas)
                draw(platformCanvas)
            }
        }
    }
}