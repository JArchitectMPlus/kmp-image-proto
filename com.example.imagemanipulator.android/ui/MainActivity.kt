package com.example.imagemanipulator.android.ui

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.imagemanipulator.android.ui.canvas.CanvasView
import com.example.imagemanipulator.android.export.AndroidCanvasExporter
import com.example.imagemanipulator.android.util.ImageHelper
import com.example.imagemanipulator.shared.CanvasViewModel
import com.example.imagemanipulator.shared.LayerViewModel
import com.example.imagemanipulator.shared.model.ImageLayer
import com.example.imagemanipulator.shared.model.TextLayer
import com.example.imagemanipulator.shared.transformation.TransformationSystem
import kotlinx.coroutines.launch
import java.util.*

class MainActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    ImageManipulatorApp()
                }
            }
        }
    }

    @Composable
    fun ImageManipulatorApp() {
        val context = LocalContext.current
        val coroutineScope = rememberCoroutineScope()

        // Use the shared CanvasViewModel, LayerViewModel, and TransformationSystem
        val canvasViewModel = remember { CanvasViewModel() }
        val layerViewModel = remember { LayerViewModel() }
        val transformationSystem = remember { TransformationSystem() }

        // Image picker launcher
        val imagePickerLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            uri?.let {
                coroutineScope.launch {
                    val bitmap = getImageFromUri(context, it)
                    bitmap?.let { bmp ->
                        val layer = ImageLayer(
                            id = UUID.randomUUID().toString(),
                            path = it.toString(),
                            image = bmp,
                            scale = 1.0f,
                            x = 100f,
                            y = 100f,
                            rotation = 0f
                        )
                        // Add to both view models
                        canvasViewModel.addLayer(layer)
                        layerViewModel.addLayer(layer)

                        // Select the new layer
                        layerViewModel.selectLayer(layer)
                    }
                }
            }
        }
        
        Column(modifier = Modifier.fillMaxSize()) {
            // Top app bar
            TopAppBar(
                title = { Text("Image Manipulator") },
                actions = {
                    IconButton(onClick = { imagePickerLauncher.launch("image/*") }) {
                        Icon(Icons.Filled.Add, contentDescription = "Add Image")
                    }
                }
            )
            
            // Canvas area
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                CanvasView(
                    viewModel = canvasViewModel,
                    layerViewModel = layerViewModel
                )
            }
            
            // Bottom controls
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                elevation = 4.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Image Manipulator", style = MaterialTheme.typography.h6)

                    Spacer(modifier = Modifier.height(16.dp))

                    // Scale controls
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Display current scale value
                        val scaleValue = layerViewModel.selectedLayer.value?.let {
                            when (it) {
                                is ImageLayer -> String.format("%.1f", it.scale)
                                is TextLayer -> String.format("%.1f", it.scale)
                                else -> "1.0"
                            }
                        } ?: "1.0"

                        Text("Scale: $scaleValue")
                        Spacer(modifier = Modifier.weight(1f))

                        // Decrease scale button
                        IconButton(onClick = {
                            layerViewModel.selectedLayer.value?.let { layer ->
                                val currentScale = when (layer) {
                                    is ImageLayer -> layer.scale
                                    is TextLayer -> layer.scale
                                    else -> 1.0f
                                }
                                // Calculate new scale and ensure it's not too small
                                val newScale = (currentScale - 0.1f).coerceAtLeast(0.5f)

                                // Use layer's specific setter to ensure state is properly updated
                                when (layer) {
                                    is ImageLayer -> {
                                        layer.scale = newScale
                                    }
                                    is TextLayer -> {
                                        layer.scale = newScale
                                    }
                                }

                                // Force UI update in both view models
                                layerViewModel.updateLayer(layer)
                                canvasViewModel.updateLayer(layer)

                                // Log the change for debugging
                                android.util.Log.d("Scale", "Decreased scale to: $newScale")
                            }
                        }) {
                            Icon(Icons.Filled.Remove, contentDescription = "Decrease Scale")
                        }

                        // Increase scale button
                        IconButton(onClick = {
                            layerViewModel.selectedLayer.value?.let { layer ->
                                val currentScale = when (layer) {
                                    is ImageLayer -> layer.scale
                                    is TextLayer -> layer.scale
                                    else -> 1.0f
                                }
                                // Calculate new scale and ensure it's not too large
                                val newScale = (currentScale + 0.1f).coerceAtMost(5.0f)

                                // Use layer's specific setter to ensure state is properly updated
                                when (layer) {
                                    is ImageLayer -> {
                                        layer.scale = newScale
                                    }
                                    is TextLayer -> {
                                        layer.scale = newScale
                                    }
                                }

                                // Force UI update in both view models
                                layerViewModel.updateLayer(layer)
                                canvasViewModel.updateLayer(layer)

                                // Log the change for debugging
                                android.util.Log.d("Scale", "Increased scale to: $newScale")
                            }
                        }) {
                            Icon(Icons.Filled.Add, contentDescription = "Increase Scale")
                        }

                        // Reset scale button
                        IconButton(onClick = {
                            layerViewModel.selectedLayer.value?.let { layer ->
                                // Set scale directly on the layer
                                when (layer) {
                                    is ImageLayer -> {
                                        layer.scale = 1.0f
                                    }
                                    is TextLayer -> {
                                        layer.scale = 1.0f
                                    }
                                }

                                // Force UI update in both view models
                                layerViewModel.updateLayer(layer)
                                canvasViewModel.updateLayer(layer)

                                // Log the change for debugging
                                android.util.Log.d("Scale", "Reset scale to: 1.0")
                            }
                        }) {
                            Icon(Icons.Filled.Refresh, contentDescription = "Reset Scale")
                        }
                    }

                    // Rotation controls
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Display current rotation value
                        val rotationValue = layerViewModel.selectedLayer.value?.let {
                            when (it) {
                                is ImageLayer -> "${it.rotation.toInt()}°"
                                is TextLayer -> "${it.rotation.toInt()}°"
                                else -> "0°"
                            }
                        } ?: "0°"

                        Text("Rotation: $rotationValue")
                        Spacer(modifier = Modifier.weight(1f))

                        // Rotate counter-clockwise button
                        IconButton(onClick = {
                            layerViewModel.selectedLayer.value?.let { layer ->
                                val currentRotation = when (layer) {
                                    is ImageLayer -> layer.rotation
                                    is TextLayer -> layer.rotation
                                    else -> 0f
                                }
                                // Calculate new rotation angle (counter-clockwise)
                                val newRotation = (currentRotation - 15f) % 360f

                                // Set rotation directly on the layer
                                when (layer) {
                                    is ImageLayer -> {
                                        layer.rotation = newRotation
                                    }
                                    is TextLayer -> {
                                        layer.rotation = newRotation
                                    }
                                }

                                // Force UI update in both view models
                                layerViewModel.updateLayer(layer)
                                canvasViewModel.updateLayer(layer)

                                // Log the change for debugging
                                android.util.Log.d("Rotation", "Rotated counter-clockwise to: $newRotation")
                            }
                        }) {
                            Icon(Icons.Filled.RotateLeft, contentDescription = "Rotate Left")
                        }

                        // Rotate clockwise button
                        IconButton(onClick = {
                            layerViewModel.selectedLayer.value?.let { layer ->
                                val currentRotation = when (layer) {
                                    is ImageLayer -> layer.rotation
                                    is TextLayer -> layer.rotation
                                    else -> 0f
                                }
                                // Calculate new rotation angle (clockwise)
                                val newRotation = (currentRotation + 15f) % 360f

                                // Set rotation directly on the layer
                                when (layer) {
                                    is ImageLayer -> {
                                        layer.rotation = newRotation
                                    }
                                    is TextLayer -> {
                                        layer.rotation = newRotation
                                    }
                                }

                                // Force UI update in both view models
                                layerViewModel.updateLayer(layer)
                                canvasViewModel.updateLayer(layer)

                                // Log the change for debugging
                                android.util.Log.d("Rotation", "Rotated clockwise to: $newRotation")
                            }
                        }) {
                            Icon(Icons.Filled.RotateRight, contentDescription = "Rotate Right")
                        }

                        // Reset rotation button
                        IconButton(onClick = {
                            layerViewModel.selectedLayer.value?.let { layer ->
                                // Set rotation directly to 0
                                when (layer) {
                                    is ImageLayer -> {
                                        layer.rotation = 0f
                                    }
                                    is TextLayer -> {
                                        layer.rotation = 0f
                                    }
                                }

                                // Force UI update in both view models
                                layerViewModel.updateLayer(layer)
                                canvasViewModel.updateLayer(layer)

                                // Log the change for debugging
                                android.util.Log.d("Rotation", "Reset rotation to: 0.0")
                            }
                        }) {
                            Icon(Icons.Filled.Refresh, contentDescription = "Reset Rotation")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Add text layer button
                    Button(
                        onClick = {
                            val textLayer = TextLayer(
                                id = UUID.randomUUID().toString(),
                                text = "New Text",
                                color = "#000000",
                                font = "Default",
                                curve = 0f,
                                x = 150f,
                                y = 150f,
                                scale = 1.0f,
                                rotation = 0f
                            )
                            canvasViewModel.addLayer(textLayer)
                            layerViewModel.addLayer(textLayer)
                            layerViewModel.selectLayer(textLayer)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Filled.TextFields, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Add Text Layer")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Add image button
                    Button(
                        onClick = { imagePickerLauncher.launch("image/*") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Filled.Image, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Add Image")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Export to Base64 button
                    Button(
                        onClick = {
                            // Create canvas exporter
                            val exporter = AndroidCanvasExporter()

                            // Export canvas to base64
                            val base64 = exporter.exportToBase64(
                                canvasViewModel.canvas.value,
                                layerViewModel.layers.value
                            )

                            // Copy to clipboard
                            val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                            val clip = android.content.ClipData.newPlainText("Canvas Export (Base64)", base64)
                            clipboard.setPrimaryClip(clip)

                            // Show toast
                            android.widget.Toast.makeText(
                                context,
                                "Canvas exported to clipboard as Base64",
                                android.widget.Toast.LENGTH_LONG
                            ).show()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Filled.Share, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Export to Base64")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Clear canvas button
                    Button(
                        onClick = {
                            canvasViewModel.clear()
                            layerViewModel.clear()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Filled.Delete, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Clear Canvas")
                    }
                }
            }
        }
    }
    
    // Helper function to get Bitmap from Uri
    private suspend fun getImageFromUri(context: android.content.Context, uri: Uri): Bitmap? {
        return ImageHelper(context).loadImage(uri.toString()) as? Bitmap
    }
}