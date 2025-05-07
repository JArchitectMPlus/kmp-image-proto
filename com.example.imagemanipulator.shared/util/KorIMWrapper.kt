package com.example.imagemanipulator.shared.util

import android.content.Context

/**
 * Cross-platform image loading and manipulation interface
 * This interface is implemented differently for each platform
 */
expect class KorIMWrapper(context: Any) {
    /**
     * Load an image from a path (could be a file path or URI)
     */
    suspend fun load(path: String): Any?
    
    /**
     * Save an image to the specified path
     */
    suspend fun save(image: Any?, path: String): Boolean
}