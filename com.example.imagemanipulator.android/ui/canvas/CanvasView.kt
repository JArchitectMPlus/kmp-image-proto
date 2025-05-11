package com.example.imagemanipulator.android.ui.canvas

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import com.example.imagemanipulator.android.ui.layers.AndroidLayerControlsView
import com.example.imagemanipulator.android.util.AKDrawBoxWrapper
import com.example.imagemanipulator.shared.CanvasViewModel
import com.example.imagemanipulator.shared.LayerViewModel
import com.example.imagemanipulator.shared.model.ImageLayer
import com.example.imagemanipulator.shared.model.Layer
import com.example.imagemanipulator.shared.model.TextLayer
import com.example.imagemanipulator.shared.transformation.PositionTransformation
import com.example.imagemanipulator.shared.transformation.RotationTransformation
import com.example.imagemanipulator.shared.transformation.ScaleTransformation
import com.example.imagemanipulator.shared.transformation.TransformationSystem

private const val TAG = "CanvasView"

@Composable
fun CanvasView(viewModel: CanvasViewModel, layerViewModel: LayerViewModel = remember { LayerViewModel() }) {
    // Collect layers from the ViewModel
    val layers by viewModel.layers.collectAsState()
    val canvasState by viewModel.canvas.collectAsState()

    // Track selected layer
    val selectedLayer by layerViewModel.selectedLayer.collectAsState()

    // Create transformation system
    val transformationSystem = remember { TransformationSystem() }

    // Track each layer to force recompositions when any layer property changes
    val layerStates by remember { mutableStateOf(mutableStateMapOf<String, Any>()) }

    // Update layer states for recomposition tracking
    // This approach creates unique state objects that Compose can detect changes to
    LaunchedEffect(layers) {
        layers.forEach { layer ->
            when (layer) {
                is ImageLayer -> {
                    layerStates[layer.id] = "${layer.scale}_${layer.rotation}_${layer.x}_${layer.y}"
                }
                is TextLayer -> {
                    layerStates[layer.id] = "${layer.scale}_${layer.rotation}_${layer.x}_${layer.y}"
                }
            }
        }
    }

    // Force redraw when selected layer changes
    LaunchedEffect(selectedLayer) {
        android.util.Log.d(TAG, "Selected layer changed: ${selectedLayer?.id}")
    }

    // Force redraw when layer view model updates
    LaunchedEffect(layerViewModel.layers.value) {
        android.util.Log.d(TAG, "LayerViewModel layers updated, count: ${layerViewModel.layers.value.size}")
        layerViewModel.layers.value.forEach { layer ->
            when (layer) {
                is ImageLayer -> {
                    val values = "${layer.scale}_${layer.rotation}_${layer.x}_${layer.y}"
                    layerStates[layer.id] = values
                    android.util.Log.d(TAG, "Layer ${layer.id}: scale=${layer.scale}, rotation=${layer.rotation}, pos=(${layer.x},${layer.y})")
                }
                is TextLayer -> {
                    val values = "${layer.scale}_${layer.rotation}_${layer.x}_${layer.y}"
                    layerStates[layer.id] = values
                    android.util.Log.d(TAG, "Layer ${layer.id}: scale=${layer.scale}, rotation=${layer.rotation}, pos=(${layer.x},${layer.y})")
                }
            }
        }
    }

    // Initialize layer view model with layers from canvas view model
    LaunchedEffect(layers) {
        // Import layers from canvas view model if layerViewModel is empty
        if (layerViewModel.layers.value.isEmpty() && layers.isNotEmpty()) {
            layers.forEach { layer ->
                layerViewModel.addLayer(layer)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Main canvas for drawing all layers
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.LightGray)
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        // Find which layer was tapped
                        val tappedLayer = findLayerAt(layers, offset)
                        if (tappedLayer != null) {
                            layerViewModel.selectLayer(tappedLayer)
                            Log.d(TAG, "Selected layer: ${tappedLayer.id}")
                        } else {
                            // Deselect current layer if tapping empty space
                            if (selectedLayer != null) {
                                layerViewModel.selectLayer(null)
                            }
                        }
                    }
                }
                // Add dragging capability for selected layer
                .pointerInput(selectedLayer?.id ?: "") {
                    var startPosition = Offset.Zero
                    var layerStartX = 0f
                    var layerStartY = 0f

                    detectDragGestures(
                        onDragStart = { offset ->
                            startPosition = offset
                            // Store initial layer position
                            selectedLayer?.let { layer ->
                                when (layer) {
                                    is ImageLayer -> {
                                        layerStartX = layer.x
                                        layerStartY = layer.y
                                    }
                                    is TextLayer -> {
                                        layerStartX = layer.x
                                        layerStartY = layer.y
                                    }
                                }
                            }
                            // Log drag start for debugging
                            android.util.Log.d("Drag", "Started dragging at: $offset")
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            selectedLayer?.let { layer ->
                                // Calculate new position
                                val newX = layerStartX + (change.position.x - startPosition.x)
                                val newY = layerStartY + (change.position.y - startPosition.y)

                                // Set position directly on the layer
                                when (layer) {
                                    is ImageLayer -> {
                                        layer.x = newX
                                        layer.y = newY
                                    }
                                    is TextLayer -> {
                                        layer.x = newX
                                        layer.y = newY
                                    }
                                }

                                // Update layer in both view models to trigger redraw
                                layerViewModel.updateLayer(layer)
                                viewModel.updateLayer(layer)

                                // Log position update for debugging
                                android.util.Log.d("Drag", "Moved to: ($newX, $newY)")

                                // Update layer states to force recomposition
                                when (layer) {
                                    is ImageLayer -> {
                                        layerStates[layer.id] = "changed_${System.currentTimeMillis()}"
                                    }
                                    is TextLayer -> {
                                        layerStates[layer.id] = "changed_${System.currentTimeMillis()}"
                                    }
                                }
                            }
                        },
                        onDragEnd = {
                            // Log drag end for debugging
                            android.util.Log.d("Drag", "Finished dragging")
                        },
                        onDragCancel = {
                            // Log drag cancel for debugging
                            android.util.Log.d("Drag", "Drag canceled")
                        }
                    )
                }
                // Use a derived modifier to help with recomposition
                .then(if (layerStates.size > 0) Modifier else Modifier)
        ) {
            drawIntoCanvas { composeCanvas ->
                // Capture layer states for this frame to force recomposition
                val states = layerStates.values.hashCode()
                android.util.Log.d(TAG, "Drawing canvas with layer states: $states, layers: ${layers.size}")

                val nativeCanvas = composeCanvas.nativeCanvas
                val paint = Paint().apply {
                    isAntiAlias = true
                }

                // Draw each layer
                layers.forEach { layer ->
                    if (layer.isVisible()) {
                        when (layer) {
                            is ImageLayer -> {
                                val bitmap = layer.image as? Bitmap
                                bitmap?.let {
                                    // Save the canvas state before applying transformations
                                    nativeCanvas.save()

                                    // Apply transformations
                                    nativeCanvas.translate(layer.x, layer.y)
                                    nativeCanvas.rotate(layer.rotation)
                                    nativeCanvas.scale(layer.scale, layer.scale)

                                    // Draw the bitmap
                                    nativeCanvas.drawBitmap(it, 0f, 0f, paint)

                                    // Draw selection outline if this layer is selected
                                    if (selectedLayer?.id == layer.id) {
                                        val outlinePaint = Paint().apply {
                                            style = Paint.Style.STROKE
                                            color = android.graphics.Color.BLUE
                                            strokeWidth = 5f
                                        }

                                        nativeCanvas.drawRect(
                                            0f, 0f,
                                            bitmap.width.toFloat(),
                                            bitmap.height.toFloat(),
                                            outlinePaint
                                        )
                                    }

                                    // Restore the canvas state
                                    nativeCanvas.restore()
                                }
                            }
                            is TextLayer -> {
                                // Save the canvas state
                                nativeCanvas.save()

                                // We'll implement text drawing here
                                val textPaint = Paint().apply {
                                    isAntiAlias = true
                                    textSize = 50f
                                    try {
                                        color = android.graphics.Color.parseColor(layer.color)
                                    } catch (e: Exception) {
                                        color = android.graphics.Color.BLACK
                                    }
                                }

                                // Save canvas state for transformations
                                nativeCanvas.save()

                                // Apply transformations
                                nativeCanvas.translate(layer.x, layer.y)
                                nativeCanvas.rotate(layer.rotation)
                                nativeCanvas.scale(layer.scale, layer.scale)

                                // Draw the text with transformations
                                nativeCanvas.drawText(
                                    layer.text,
                                    0f,
                                    0f,
                                    textPaint
                                )

                                // Draw selection outline if this layer is selected
                                if (selectedLayer?.id == layer.id) {
                                    val outlinePaint = Paint().apply {
                                        style = Paint.Style.STROKE
                                        color = android.graphics.Color.BLUE
                                        strokeWidth = 5f
                                    }

                                    // Approximate text bounds
                                    val textWidth = textPaint.measureText(layer.text)
                                    val textHeight = textPaint.textSize

                                    nativeCanvas.drawRect(
                                        -10f,
                                        -textHeight,
                                        textWidth + 10f,
                                        10f,
                                        outlinePaint
                                    )
                                }

                                // Restore the canvas state
                                nativeCanvas.restore()
                            }
                        }
                    }
                }
            }
        }

        // No overlay controls on the canvas area
    }
}

// Helper function to find which layer was tapped
private fun findLayerAt(layers: List<Layer>, offset: Offset): Layer? {
    // Go through layers in reverse order (top to bottom)
    for (i in layers.indices.reversed()) {
        val layer = layers[i]
        if (!layer.isVisible()) continue
        
        when (layer) {
            is ImageLayer -> {
                val bitmap = layer.image as? Bitmap ?: continue
                
                // Calculate the bounds of this layer with transformations
                val centerX = layer.x
                val centerY = layer.y
                
                // Simple bounding box check for now - this could be more sophisticated
                val width = bitmap.width * layer.scale
                val height = bitmap.height * layer.scale
                
                val left = centerX - (width / 2)
                val top = centerY - (height / 2)
                val right = centerX + (width / 2)
                val bottom = centerY + (height / 2)
                
                if (offset.x >= left && offset.x <= right && 
                    offset.y >= top && offset.y <= bottom) {
                    return layer
                }
            }
            is TextLayer -> {
                // Approximate text bounds
                val paint = Paint().apply {
                    textSize = 50f * layer.scale
                }
                
                val textWidth = paint.measureText(layer.text) 
                val textHeight = paint.textSize
                
                // Create a bounding box around the text
                val left = layer.x - 10f
                val top = layer.y - textHeight
                val right = layer.x + textWidth + 10f
                val bottom = layer.y + 10f
                
                if (offset.x >= left && offset.x <= right && 
                    offset.y >= top && offset.y <= bottom) {
                    return layer
                }
            }
        }
    }
    return null
}