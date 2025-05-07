package com.example.imagemanipulator.android

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.imagemanipulator.android.util.ImageHelper
import com.example.imagemanipulator.shared.transformation.ImageTransformation
import com.example.imagemanipulator.shared.transformation.Point

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
}

@Composable
fun ImageManipulatorApp() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    // State for the loaded image
    var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    
    // Create an instance of the ImageHelper
    val imageHelper = remember { ImageHelper(context) }
    
    // Create a shared transformation manager with state observation
    val transformation = remember { ImageTransformation() }
    
    // Create observable states for each transformation property to trigger recomposition
    var scale by remember { mutableStateOf(transformation.scale) }
    var rotation by remember { mutableStateOf(transformation.rotation) }
    var offset by remember { mutableStateOf(transformation.offset) }
    
    // Image picker launcher
    val pickImage = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            coroutineScope.launch {
                // Use our shared ImageHelper to load the image
                val bitmap = imageHelper.loadImage(uri.toString()) as? Bitmap
                imageBitmap = bitmap
                
                // Reset transformations when loading a new image
                transformation.reset()
                // Update all state variables
                scale = transformation.scale
                rotation = transformation.rotation
                offset = transformation.offset
            }
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Image Manipulator App",
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
            imageBitmap?.let { bitmap ->
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectTransformGestures { _, pan, zoom, rotationChange ->
                                // Use shared transformation logic
                                transformation.applyScale(zoom)
                                transformation.applyRotation(rotationChange)
                                transformation.applyTranslation(pan.x, pan.y)
                                
                                // Update observable state to trigger recomposition
                                scale = transformation.scale
                                rotation = transformation.rotation
                                offset = transformation.offset
                            }
                        }
                ) {
                    val canvasCenter = Offset(size.width / 2, size.height / 2)
                    
                    // Apply transformations
                    rotate(rotation, pivot = canvasCenter) {
                        scale(scale, pivot = canvasCenter) {
                            // Draw the bitmap at the center with offsets
                            drawImage(
                                bitmap.asImageBitmap(),
                                topLeft = Offset(
                                    canvasCenter.x - bitmap.width / 2 + offset.x,
                                    canvasCenter.y - bitmap.height / 2 + offset.y
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
                    Text("Scale: ${String.format("%.1f", scale)}")
                    Row {
                        IconButton(onClick = { 
                            transformation.setScale(transformation.scale - 0.1f)
                            scale = transformation.scale 
                        }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Decrease scale")
                        }
                        IconButton(onClick = { 
                            transformation.setScale(transformation.scale + 0.1f)
                            scale = transformation.scale
                        }) {
                            Icon(Icons.Filled.Add, contentDescription = "Increase scale")
                        }
                        IconButton(onClick = { 
                            transformation.setScale(1f)
                            scale = transformation.scale
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
                    Text("Rotation: ${String.format("%.0f", rotation)}°")
                    Row {
                        IconButton(onClick = { 
                            transformation.applyRotation(-15f)
                            rotation = transformation.rotation
                        }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Rotate counterclockwise")
                        }
                        IconButton(onClick = { 
                            transformation.applyRotation(15f)
                            rotation = transformation.rotation
                        }) {
                            Icon(Icons.Filled.Add, contentDescription = "Rotate clockwise")
                        }
                        IconButton(onClick = { 
                            transformation.setRotation(0f)
                            rotation = transformation.rotation
                        }) {
                            Icon(Icons.Filled.Refresh, contentDescription = "Reset rotation")
                        }
                    }
                }
                
                // Reset button
                Button(
                    onClick = {
                        transformation.reset()
                        // Update all state variables
                        scale = transformation.scale
                        rotation = transformation.rotation
                        offset = transformation.offset
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Reset All")
                }
            }
        }
        
        // Load image button
        Button(
            onClick = { pickImage.launch("image/*") },
            modifier = Modifier
                .padding(vertical = 16.dp)
                .fillMaxWidth()
        ) {
            Text("Load Image")
        }
    }
}