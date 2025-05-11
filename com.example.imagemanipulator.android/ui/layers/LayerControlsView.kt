package com.example.imagemanipulator.shared.ui.layers

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
import androidx.compose.material.Slider
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.imagemanipulator.shared.LayerViewModel
import com.example.imagemanipulator.shared.model.ImageLayer

@Composable
actual fun LayerControlsView(layerViewModel: LayerViewModel) {
    val layers by layerViewModel.layers.collectAsState()
    val selectedLayer by layerViewModel.selectedLayer.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.LightGray.copy(alpha = 0.3f))
            .padding(16.dp)
    ) {
        Text(
            text = "Layer Controls",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Divider()
        
        if (selectedLayer == null) {
            Text(
                "No layer selected",
                modifier = Modifier.padding(vertical = 8.dp)
            )
        } else {
            // Layer is selected, show controls
            val imageLayer = selectedLayer as? ImageLayer
            
            if (imageLayer != null) {
                Spacer(modifier = Modifier.height(16.dp))
                
                // Rotation control
                Text("Rotation: ${imageLayer.rotation.toInt()}°")
                Slider(
                    value = imageLayer.rotation,
                    onValueChange = { layerViewModel.setRotation(it) },
                    valueRange = 0f..360f,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Scale control
                Text("Scale: ${String.format("%.2f", imageLayer.scale)}x")
                Slider(
                    value = imageLayer.scale,
                    onValueChange = { layerViewModel.setScale(it) },
                    valueRange = 0.1f..2f,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Visibility control
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Visible")
                    Spacer(modifier = Modifier.width(8.dp))
                    Switch(
                        checked = imageLayer.isVisible(),
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
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
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
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(backgroundColor)
                        .padding(8.dp)
                        .run {
                            if (!isSelected) {
                                this.background(Color.Transparent)
                            } else {
                                this
                            }
                        }
                ) {
                    Text(
                        "Layer ${layer.id}",
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