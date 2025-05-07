package com.example.imagemanipulator.ios.ui.canvas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.IntSize
import com.example.imagemanipulator.ios.drawing.IOSPlatformDrawBox
import com.example.imagemanipulator.ios.drawing.IOSPlatformCanvas
import com.example.imagemanipulator.shared.CanvasViewModel
import com.example.imagemanipulator.shared.model.ImageLayer
import com.example.imagemanipulator.shared.model.Layer
import com.example.imagemanipulator.shared.model.TextLayer
import platform.CoreGraphics.CGContextRef
import platform.CoreGraphics.CGPointMake
import platform.CoreGraphics.CGRect
import platform.CoreGraphics.CGRectMake
import platform.CoreGraphics.CGSize
import platform.CoreGraphics.CGContextSetFillColorWithColor
import platform.CoreGraphics.CGContextFillRect
import platform.UIKit.UIColor
import platform.UIKit.UIView
import platform.UIKit.UIImage
import platform.UIKit.UIImageView
import platform.UIKit.addSubview
import platform.UIKit.setNeedsDisplay
import kotlin.math.max
import kotlin.math.min

class CanvasView(
    private val canvasViewModel: CanvasViewModel
) : UIView(CGRectMake(0.0, 0.0, 0.0, 0.0)) {

    private val platformDrawBox = IOSPlatformDrawBox()
    private var canvasSize = IntSize(0, 0)

    init {
        backgroundColor = UIColor.whiteColor
    }

    override fun drawRect(rect: CGRect) {
        super.drawRect(rect)
        val iosPlatformCanvas = IOSPlatformCanvas()
        val imageLayer = ImageLayer(position = Offset(100f, 100f), size = IntSize(200, 200))
        platformDrawBox.drawOnCanvas(iosPlatformCanvas) { platformCanvas ->
            drawLayer(it, it.getContext()!!, imageLayer)
            drawLayers(it)
        }
    }

    fun updateLayers() {
        setNeedsDisplay()
    }

    private fun drawLayers(canvas: IOSPlatformCanvas) {
        val ctx = canvas.getContext() ?: return
        val layers = canvasViewModel.canvas.value?.layers ?: return
        for (layer in layers) {
            drawLayer(canvas, ctx, layer)
        }
    }

    private fun drawLayer(canvas: IOSPlatformCanvas, ctx: CGContextRef, layer: Layer) {
        when (layer) {
            is ImageLayer -> {
                canvas.drawLayer(layer, layer.position, layer.size)
            }
            is TextLayer -> {
                drawTextLayer(canvas, ctx, layer)
            }
        }
    }

    private fun drawTextLayer(canvas: IOSPlatformCanvas, ctx: CGContextRef, layer: TextLayer) {
        val color = layer.color
        CGContextSetFillColorWithColor(ctx, color.toUIColor().CGColor)
        val textRect = CGRectMake(layer.position.x.toDouble(), layer.position.y.toDouble(), 100.0, 100.0)
        CGContextFillRect(ctx, textRect)
    }
}

fun Color.toUIColor(): UIColor = UIColor(
    red = (this.red),
    green = (this.green),
    blue = (this.blue),
    alpha = this.alpha
)