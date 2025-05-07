package com.example.imagemanipulator.android.ui

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.Composable
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            MaterialTheme {
                Surface {
                    SimpleApp()
                }
            }
        }
    }

    @Composable
    fun SimpleApp() {
        Column {
            androidx.compose.material.Text("DecoPac POC")
            androidx.compose.material.Button(onClick = {
                // Button click handler
            }) {
                androidx.compose.material.Text("Load Image")
            }
        }
    }
}