package com.example.imagemanipulator.shared.util

/**
 * Interface for platform-specific image loading and processing
 * This is a simplified interface for demo purposes
 */
interface ImageHelperInterface {
    /**
     * Load an image from a path (could be a file path or content URI)
     * @param path The path or URI to the image
     * @return A platform-specific image object or null if loading failed
     */
    suspend fun loadImage(path: String): Any?
    
    /**
     * Save an image to a specified path
     * @param image The image to save
     * @param path The path to save the image to
     * @return True if saving succeeded, false otherwise
     */
    suspend fun saveImage(image: Any?, path: String): Boolean
    
    /**
     * Get the width of an image
     */
    fun getImageWidth(image: Any?): Int
    
    /**
     * Get the height of an image
     */
    fun getImageHeight(image: Any?): Int
}