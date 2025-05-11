package com.example.imagemanipulator.android.ui.layers

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Slider
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.imagemanipulator.shared.LayerViewModel
import com.example.imagemanipulator.shared.model.ImageLayer
import com.example.imagemanipulator.shared.model.TextLayer
import java.util.UUID

@Composable
fun AndroidLayerControlsView(
    layerViewModel: LayerViewModel,
    modifier: Modifier = Modifier
) {
    val layers by layerViewModel.layers.collectAsState()
    val selectedLayer by layerViewModel.selectedLayer.collectAsState()
    val context = LocalContext.current
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.9f))
            .padding(16.dp)
    ) {
        Text(
            text = "Layer Controls",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Divider()
        
        // Add Text Layer button
        Button(
            onClick = {
                val newTextLayer = TextLayer(
                    id = UUID.randomUUID().toString(),
                    text = "New Text",
                    color = "#000000",
                    font = "Default",
                    curve = 0f,
                    x = 100f,
                    y = 100f,
                    scale = 1f,
                    rotation = 0f
                )
                layerViewModel.addLayer(newTextLayer)
                layerViewModel.selectLayer(newTextLayer)
                Toast.makeText(context, "Added text layer", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("Add Text Layer")
        }
        
        // Layer properties controls
        if (selectedLayer == null) {
            Text(
                "No layer selected",
                modifier = Modifier.padding(vertical = 8.dp)
            )
        } else {
            Spacer(modifier = Modifier.height(16.dp))
            
            // Position controls are still functional through dragging, but readouts are hidden
            
            // Rotation control
            when (selectedLayer) {
                is ImageLayer -> {
                    val imageLayer = selectedLayer as ImageLayer
                    Text("Rotation: ${imageLayer.rotation.toInt()}°")
                    Slider(
                        value = imageLayer.rotation,
                        onValueChange = { layerViewModel.setRotation(it) },
                        valueRange = 0f..360f,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                is TextLayer -> {
                    val textLayer = selectedLayer as TextLayer
                    Text("Rotation: ${textLayer.rotation.toInt()}°")
                    Slider(
                        value = textLayer.rotation,
                        onValueChange = { layerViewModel.setRotation(it) },
                        valueRange = 0f..360f,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Scale control
            when (selectedLayer) {
                is ImageLayer -> {
                    val imageLayer = selectedLayer as ImageLayer
                    Text("Scale: ${String.format("%.2f", imageLayer.scale)}x")
                    Slider(
                        value = imageLayer.scale,
                        onValueChange = { layerViewModel.setScale(it) },
                        valueRange = 0.1f..2f,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                is TextLayer -> {
                    val textLayer = selectedLayer as TextLayer
                    Text("Scale: ${String.format("%.2f", textLayer.scale)}x")
                    Slider(
                        value = textLayer.scale,
                        onValueChange = { layerViewModel.setScale(it) },
                        valueRange = 0.1f..2f,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Text specific controls
            if (selectedLayer is TextLayer) {
                val textLayer = selectedLayer as TextLayer
                
                Divider()
                Spacer(modifier = Modifier.height(8.dp))
                Text("Text Layer Properties", fontWeight = FontWeight.Bold)
                
                // Text content
                var text by remember { mutableStateOf(textLayer.text) }
                
                OutlinedTextField(
                    value = text,
                    onValueChange = {
                        text = it
                        layerViewModel.setText(it)
                    },
                    label = { Text("Text Content") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Text color
                var color by remember { mutableStateOf(textLayer.color) }
                
                OutlinedTextField(
                    value = color,
                    onValueChange = {
                        color = it
                        layerViewModel.setTextColor(it)
                    },
                    label = { Text("Text Color (hex)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Visibility control
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Visible")
                Spacer(modifier = Modifier.width(8.dp))
                Switch(
                    checked = selectedLayer?.isVisible() ?: true,
                    onCheckedChange = { layerViewModel.toggleVisibility() }
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Delete layer button
            Button(
                onClick = { layerViewModel.deleteSelectedLayer() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Delete Layer")
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        Divider()
        
        // Layer list display
        Text(
            text = "Layers (${layers.size})",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        
        if (layers.isEmpty()) {
            Text("No layers available")
        } else {
            layers.forEach { layer ->
                val isSelected = layer.id == selectedLayer?.id
                val backgroundColor = if (isSelected) Color.Blue.copy(alpha = 0.2f) else Color.Transparent
                
                val layerName = when (layer) {
                    is ImageLayer -> "Image Layer ${layer.id.take(4)}"
                    is TextLayer -> "Text: ${(layer as TextLayer).text.take(10)}..."
                    else -> "Layer ${layer.id.take(4)}"
                }
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(backgroundColor)
                        .padding(8.dp)
                ) {
                    Text(
                        layerName,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                    
                    Spacer(modifier = Modifier.weight(1f))
                    
                    Button(
                        onClick = { layerViewModel.selectLayer(layer) }
                    ) {
                        Text("Select")
                    }
                }
            }
        }
    }
}