package com.example.imagemanipulator.ios.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.imagemanipulator.ios.util.ImageHelper
import com.example.imagemanipulator.shared.transformation.ImageTransformation
import kotlinx.coroutines.launch
import platform.UIKit.UIImage

/**
 * Converts a UIImage to an ImageBitmap
 * This would normally be an extension function, but for this demo we'll leave it as a 
 * placeholder since we're focusing on the core functionality.
 */
fun UIImage.toImageBitmap(): ImageBitmap {
    // This is a simplified implementation for demo purposes
    val width = this.size.width.toInt()
    val height = this.size.height.toInt()
    
    // Create a simple solid color bitmap for demonstration
    val bitmap = ImageBitmap(width, height)
    
    return bitmap
}

/**
 * Main image manipulator composable for iOS
 */
@Composable
fun ImageManipulatorView(onPickImage: () -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    
    // State for the loaded image
    var currentImage by remember { mutableStateOf<UIImage?>(null) }
    var currentImageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    
    // Create an instance of the ImageHelper
    val imageHelper = remember { ImageHelper() }
    
    // Create a shared transformation manager
    val transformation = remember { ImageTransformation() }
    
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Image Manipulator for iOS",
                style = MaterialTheme.typography.h5,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            // Image display area
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.LightGray)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                currentImageBitmap?.let { bitmap ->
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(Unit) {
                                detectTransformGestures { _, pan, zoom, rotationChange ->
                                    // Use shared transformation logic
                                    transformation.applyScale(zoom)
                                    transformation.applyRotation(rotationChange)
                                    transformation.applyTranslation(pan.x, pan.y)
                                }
                            }
                    ) {
                        val canvasCenter = Offset(size.width / 2, size.height / 2)
                        
                        // Apply transformations
                        rotate(transformation.rotation, pivot = canvasCenter) {
                            scale(transformation.scale, pivot = canvasCenter) {
                                // Draw the bitmap at the center with offsets
                                drawImage(
                                    bitmap,
                                    topLeft = Offset(
                                        canvasCenter.x - bitmap.width / 2 + transformation.offset.x,
                                        canvasCenter.y - bitmap.height / 2 + transformation.offset.y
                                    )
                                )
                            }
                        }
                    }
                } ?: run {
                    Text("No image loaded. Tap 'Load Image' to begin.")
                }
            }
            
            // Controls section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                elevation = 4.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Scale controls
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Scale: ${String.format("%.1f", transformation.scale)}")
                        Row {
                            IconButton(onClick = { 
                                transformation.setScale(transformation.scale - 0.1f) 
                            }) {
                                Icon(Icons.Filled.Remove, contentDescription = "Decrease scale")
                            }
                            IconButton(onClick = { 
                                transformation.setScale(transformation.scale + 0.1f) 
                            }) {
                                Icon(Icons.Filled.Add, contentDescription = "Increase scale")
                            }
                            IconButton(onClick = { 
                                transformation.setScale(1f) 
                            }) {
                                Icon(Icons.Filled.Refresh, contentDescription = "Reset scale")
                            }
                        }
                    }
                    
                    // Rotation controls
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Rotation: ${String.format("%.0f", transformation.rotation)}°")
                        Row {
                            IconButton(onClick = { 
                                transformation.applyRotation(-15f) 
                            }) {
                                Icon(Icons.Filled.Remove, contentDescription = "Rotate counterclockwise")
                            }
                            IconButton(onClick = { 
                                transformation.applyRotation(15f) 
                            }) {
                                Icon(Icons.Filled.Add, contentDescription = "Rotate clockwise")
                            }
                            IconButton(onClick = { 
                                transformation.setRotation(0f) 
                            }) {
                                Icon(Icons.Filled.Refresh, contentDescription = "Reset rotation")
                            }
                        }
                    }
                    
                    // Reset button
                    Button(
                        onClick = {
                            transformation.reset()
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Reset All")
                    }
                }
            }
            
            // Load image button
            Button(
                onClick = onPickImage,
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .fillMaxWidth()
            ) {
                Text("Load Image")
            }
        }
    }
    
    // Function to update the displayed image
    fun loadImage(uiImage: UIImage?) {
        coroutineScope.launch {
            currentImage = uiImage
            
            // Convert UIImage to ImageBitmap
            currentImageBitmap = uiImage?.toImageBitmap()
            
            // Reset transformations
            transformation.reset()
        }
    }
}