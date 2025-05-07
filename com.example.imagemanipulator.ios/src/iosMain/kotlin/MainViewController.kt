package com.example.imagemanipulator.ios

import androidx.compose.foundation.layout.Column
import androidx.compose.ui.window.ComposeUIViewController
import com.example.imagemanipulator.shared.App
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController = ComposeUIViewController {
    Column {
        App()
    }
}