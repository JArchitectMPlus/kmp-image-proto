package com.example.imagemanipulator.android.ui

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.Composable
import com.example.imagemanipulator.shared.model.Canvas
import com.example.imagemanipulator.shared.model.ImageLayer
import com.example.imagemanipulator.shared.model.TextLayer
import com.example.imagemanipulator.shared.ui.canvas.CanvasView
import com.example.imagemanipulator.shared.ui.canvas.LayerOptionsView
import com.example.imagemanipulator.shared.ui.export.ExportView
import com.example.imagemanipulator.shared.ui.layers.LayerControlsView
import com.example.imagemanipulator.shared.ui.layers.LayerListView
import com.example.imagemanipulator.shared.CanvasViewModel
import com.example.imagemanipulator.shared.LayerViewModel
import com.example.imagemanipulator.shared.util.KorIMWrapper

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface {
                    App()
                }
            }
        }
    }

    @Composable
    fun App() {
        val context = LocalContext.current
        val korimWrapper = KorIMWrapper(context)
        val layers = mutableListOf<com.example.imagemanipulator.shared.model.Layer>()
        val canvas = Canvas(800, 600, "4:3", layers)
        val canvasViewModel = CanvasViewModel(canvas)
        val layerViewModel = LayerViewModel(layers)

        val pickImage = rememberLauncherForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            uri?.let {
                addImageFromUri(it)
            }
        }

        Column {
            CanvasView(canvasViewModel)
            LayerListView(layerViewModel) {
                pickImage.launch("image/*")
            }
            LayerControlsView(layerViewModel)
            LayerOptionsView(layerViewModel)
            ExportView(canvasViewModel)
        }
    }

    private fun addImageFromUri(uri: Uri) {
        // Implementation for loading and adding image from Uri
        // This will likely involve using the KorIMWrapper and updating the canvasViewModel and layerViewModel
    }

    private fun setSelectedImageLayer(imageLayer: ImageLayer?) {
        // Implementation for setting the selected image layer
    }
}