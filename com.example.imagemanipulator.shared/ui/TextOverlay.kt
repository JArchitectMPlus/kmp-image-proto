package com.example.imagemanipulator.shared.ui

import com.example.imagemanipulator.shared.transformation.Point

/**
 * Data class to hold text overlay information
 */
data class TextOverlay(
    val text: String,
    val position: Point,
    val color: Any = Any(), // This will be Color in Android and UIColor in iOS
    val size: Float = 24f,
    val id: Long = kotlin.random.Random.nextLong() // Unique identifier
)