package com.example.imagemanipulator.ios.util

import com.example.imagemanipulator.shared.util.ImageHelperInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import platform.UIKit.*
import platform.Foundation.*
import platform.CoreGraphics.*

/**
 * iOS implementation of ImageHelperInterface
 */
class ImageHelper : ImageHelperInterface {
    
    override suspend fun loadImage(path: String): Any? = withContext(Dispatchers.Default) {
        try {
            // Handle different path types
            val image = when {
                path.startsWith("file://") -> {
                    val url = NSURL.URLWithString(path)
                    UIImage.imageWithContentsOfFile(url?.path ?: "")
                }
                path.startsWith("/") -> {
                    // Absolute file path
                    UIImage.imageWithContentsOfFile(path)
                }
                else -> {
                    // Try to load as a URL
                    val url = NSURL.URLWithString(path)
                    val data = NSData.dataWithContentsOfURL(url)
                    data?.let { UIImage.imageWithData(it) }
                }
            }
            
            return@withContext image
        } catch (e: Exception) {
            println("Error loading image: ${e.message}")
            return@withContext null
        }
    }
    
    override suspend fun saveImage(image: Any?, path: String): Boolean = withContext(Dispatchers.Default) {
        if (image !is UIImage) return@withContext false
        
        try {
            // Convert to PNG data
            val pngData = UIImagePNGRepresentation(image) ?: return@withContext false
            
            // Save to file
            val fileURL = NSURL.fileURLWithPath(path)
            return@withContext pngData.writeToURL(fileURL, true)
        } catch (e: Exception) {
            println("Error saving image: ${e.message}")
            return@withContext false
        }
    }
    
    override fun getImageWidth(image: Any?): Int {
        return if (image is UIImage) image.size.width.toInt() else 0
    }
    
    override fun getImageHeight(image: Any?): Int {
        return if (image is UIImage) image.size.height.toInt() else 0
    }
}