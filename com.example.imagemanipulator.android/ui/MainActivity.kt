package com.example.imagemanipulator.android.ui


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.Modifier
import com.example.imagemanipulator.android.ui.canvas.CanvasView
import com.example.imagemanipulator.android.ui.canvas.LayerOptionsView
import com.example.imagemanipulator.android.ui.export.ExportView
import com.example.imagemanipulator.android.ui.layers.LayerControlsView
import com.example.imagemanipulator.android.ui.layers.LayerListView
import com.example.imagemanipulator.android.viewmodel.CanvasViewModel
import com.example.imagemanipulator.android.viewmodel.LayerViewModel
import com.example.imagemanipulator.shared.model.Canvas
import com.example.imagemanipulator.shared.model.ImageLayer
import com.example.imagemanipulator.shared.model.Layer
import com.example.imagemanipulator.shared.model.TextLayer


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface {
                    val canvas = Canvas(width = 800, height = 600, aspectRatio = "3:2", layers = mutableListOf())
                    val canvasViewModel = CanvasViewModel(canvas)

                    val layers: MutableList<Layer> = mutableListOf()
                    // Add some sample data
                    layers.add(ImageLayer(image = Any(), id = "image1")) // Replace Any() with a real image later
                    layers.add(TextLayer(id = "text1", text = "Sample Text", color = "#000000", font = "Arial", curve = 0f))

                    val layerViewModel = LayerViewModel(layers)

                    Column(modifier = Modifier.fillMaxSize()) {
                        CanvasView(canvasViewModel)
                        LayerListView(layerViewModel)
                        LayerControlsView(layerViewModel)
                        LayerOptionsView(layerViewModel)
                        ExportView(canvasViewModel)
                    }
                }
            }
        }
    }
}
