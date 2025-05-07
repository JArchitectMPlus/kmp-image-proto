package com.example.imagemanipulator.ios.drawing

import com.example.imagemanipulator.shared.drawing.PlatformCanvas
import com.example.imagemanipulator.shared.drawing.PlatformDrawBox

actual class IOSPlatformDrawBox : PlatformDrawBox {
    actual override fun drawOnCanvas(platformCanvas: PlatformCanvas, draw: PlatformCanvas.() -> Unit) {
        draw(platformCanvas)
    }
}