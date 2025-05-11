package com.example.imagemanipulator.ios.ui.canvas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import com.example.imagemanipulator.ios.drawing.IOSPlatformDrawBox
import com.example.imagemanipulator.ios.drawing.IOSPlatformCanvas
import com.example.imagemanipulator.ios.util.getX
import com.example.imagemanipulator.ios.util.getY
import com.example.imagemanipulator.shared.CanvasViewModel
import com.example.imagemanipulator.shared.model.ImageLayer
import com.example.imagemanipulator.shared.model.Layer
import com.example.imagemanipulator.shared.model.TextLayer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.CValue
import platform.CoreGraphics.*
import platform.UIKit.*

@ExperimentalForeignApi
class CanvasView(
    private val canvasViewModel: CanvasViewModel
) : UIView(CGRectMake(0.0, 0.0, 300.0, 300.0)) {

    private val platformDrawBox = IOSPlatformDrawBox()
    private var canvasSize = IntSize(300, 300)
    
    // Track the selected layer ID
    private var selectedLayerId: String? = null

    init {
        backgroundColor = UIColor.whiteColor
        
        // Listen for touch events
        userInteractionEnabled = true
    }

    @ExperimentalForeignApi
    override fun drawRect(rect: CValue<CGRect>) {
        super.drawRect(rect)
        val iosPlatformCanvas = IOSPlatformCanvas()

        platformDrawBox.drawOnCanvas(iosPlatformCanvas) { canvas ->
            // Draw all layers from the canvas model
            drawLayers(canvas)
        }
    }
    
    // Update the view when layers change
    fun updateLayers() {
        setNeedsDisplay()
    }
    
    // Update the view when a layer property changes
    fun updateLayerProperty(layerId: String) {
        setNeedsDisplay()
    }
    
    // Handle taps on the view
    @ExperimentalForeignApi
    override fun touchesBegan(touches: Set<*>, withEvent: UIEvent?) {
        super.touchesBegan(touches, withEvent)
        
        // Get the touch location
        val touch = touches.firstOrNull() as? UITouch ?: return
        val location = touch.locationInView(this)
        
        // Check if any layer was tapped
        val layers = canvasViewModel.canvas.value.layers
        for (layer in layers) {
            when (layer) {
                is ImageLayer -> {
                    // Simple bounding box check for image layers
                    if (locationInImageLayer(location, layer)) {
                        // Select the layer
                        selectedLayerId = layer.id
                        break
                    }
                }
                is TextLayer -> {
                    // Simple bounding box check for text layers
                    if (locationInTextLayer(location, layer)) {
                        // Select the layer
                        selectedLayerId = layer.id
                        break
                    }
                }
            }
        }
        
        // Redraw
        setNeedsDisplay()
    }
    
    // Check if the location is inside an image layer
    private fun locationInImageLayer(location: CValue<CGPoint>, layer: ImageLayer): Boolean {
        // Access coordinates using our extension functions
        val locationX = location.getX()
        val locationY = location.getY()

        val minX = layer.x.toDouble()
        val minY = layer.y.toDouble()
        val maxX = minX + (200.0 * layer.scale)
        val maxY = minY + (200.0 * layer.scale)

        return locationX >= minX && locationX <= maxX &&
               locationY >= minY && locationY <= maxY
    }

    // Check if the location is inside a text layer
    private fun locationInTextLayer(location: CValue<CGPoint>, layer: TextLayer): Boolean {
        // Access coordinates using our extension functions
        val locationX = location.getX()
        val locationY = location.getY()

        val minX = layer.x.toDouble()
        val minY = layer.y.toDouble()
        val maxX = minX + (100.0 * layer.scale)
        val maxY = minY + (50.0 * layer.scale)

        return locationX >= minX && locationX <= maxX &&
               locationY >= minY && locationY <= maxY
    }

    @ExperimentalForeignApi
    private fun drawLayers(canvas: IOSPlatformCanvas) {
        val ctx = canvas.getContext() ?: return
        val layers = canvasViewModel.canvas.value.layers
        for (layer in layers) {
            drawLayer(canvas, ctx, layer)
        }
    }

    @ExperimentalForeignApi
    private fun drawLayer(canvas: IOSPlatformCanvas, ctx: CGContextRef, layer: Layer) {
        when (layer) {
            is ImageLayer -> {
                // Apply transformations and draw the image layer
                canvas.drawLayerWithTransform(
                    layer,
                    layer.x,
                    layer.y,
                    200f, // Assuming a default width if not specified
                    200f, // Assuming a default height if not specified
                    layer.rotation,
                    layer.scale,
                    layer.scale
                )
                
                // If this is the selected layer, draw a selection border
                if (layer.id == selectedLayerId) {
                    drawImageSelectionBorder(ctx, layer)
                }
            }
            is TextLayer -> {
                drawTextLayer(canvas, ctx, layer)
                
                // If this is the selected layer, draw a selection border
                if (layer.id == selectedLayerId) {
                    drawTextSelectionBorder(ctx, layer)
                }
            }
        }
    }

    @ExperimentalForeignApi
    private fun drawImageSelectionBorder(ctx: CGContextRef, layer: ImageLayer) {
        // Set stroke color to blue
        CGContextSetStrokeColorWithColor(ctx, UIColor.blueColor.CGColor)
        CGContextSetLineWidth(ctx, 2.0)
        
        // Draw rectangle around the image layer
        val borderRect = CGRectMake(
            layer.x.toDouble() - 2.0,
            layer.y.toDouble() - 2.0,
            204.0 * layer.scale, // Add padding for selection border
            204.0 * layer.scale
        )
        
        CGContextStrokeRect(ctx, borderRect)
    }
    
    @ExperimentalForeignApi
    private fun drawTextSelectionBorder(ctx: CGContextRef, layer: TextLayer) {
        // Set stroke color to blue
        CGContextSetStrokeColorWithColor(ctx, UIColor.blueColor.CGColor)
        CGContextSetLineWidth(ctx, 2.0)
        
        // Draw rectangle around the text layer
        val borderRect = CGRectMake(
            layer.x.toDouble() - 2.0,
            layer.y.toDouble() - 2.0,
            104.0 * layer.scale, // Add padding for selection border
            54.0 * layer.scale
        )
        
        CGContextStrokeRect(ctx, borderRect)
    }

    @ExperimentalForeignApi
    private fun drawTextLayer(canvas: IOSPlatformCanvas, ctx: CGContextRef, layer: TextLayer) {
        // Use a default color
        val color = UIColor.blackColor

        CGContextSetFillColorWithColor(ctx, color.CGColor)

        // Draw a rectangle for the text background (as a placeholder)
        val textRect = CGRectMake(
            layer.x.toDouble(),
            layer.y.toDouble(),
            (100.0 * layer.scale),
            (50.0 * layer.scale)
        )

        CGContextFillRect(ctx, textRect)
    }
}