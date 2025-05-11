package com.example.imagemanipulator.ios.util

import com.example.imagemanipulator.shared.util.KorIMWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import platform.UIKit.UIImage
import platform.UIKit.UIImagePNGRepresentation
import platform.Foundation.NSData
import platform.Foundation.NSURL
import platform.Foundation.writeToURL
import platform.Foundation.NSFileManager
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSUserDomainMask
import kotlinx.cinterop.ExperimentalForeignApi
import com.example.imagemanipulator.ios.util.getWidth
import com.example.imagemanipulator.ios.util.getHeight

/**
 * iOS-specific implementation of KorIMWrapper
 * Handles loading and saving images in a platform-specific way
 */
@ExperimentalForeignApi
class KorIMWrapper(private val context: Any? = null) {

    /**
     * Load an image from a path (could be a file path or URI)
     */
    suspend fun load(path: String): Any? = withContext(Dispatchers.Default) {
        try {
            // Try loading as a file path
            // Create a simple wrapper that directly loads an image from a path
            val image = UIImage.imageWithContentsOfFile(path)

            if (image != null) {
                // Return the image if we could load it from the path
                return@withContext image
            } else {
                // If it's not a file path, try as a resource name
                return@withContext UIImage.imageNamed(path)
            }
        } catch (e: Exception) {
            println("Error loading image: ${e.message}")
            // Fall back to a bundled asset if available
            return@withContext UIImage.imageNamed("placeholder")
        }
    }

    /**
     * Save an image to the specified path
     */
    suspend fun save(image: Any?, path: String): Boolean = withContext(Dispatchers.Default) {
        if (image !is UIImage) return@withContext false

        try {
            // Convert to PNG data
            val pngData = UIImagePNGRepresentation(image) ?: return@withContext false

            // Create directory if needed
            val directory = path.substringBeforeLast("/", "")
            if (directory.isNotEmpty()) {
                NSFileManager.defaultManager.createDirectoryAtPath(
                    directory,
                    true,
                    null,
                    null
                )
            }

            // Save to file
            val fileURL = NSURL.fileURLWithPath(path)
            return@withContext pngData.writeToURL(fileURL, true)
        } catch (e: Exception) {
            println("Error saving image: ${e.message}")
            return@withContext false
        }
    }

    /**
     * Get the Documents directory path
     */
    @ExperimentalForeignApi
    fun getDocumentsDirectory(): String {
        val paths = NSSearchPathForDirectoriesInDomains(
            NSDocumentDirectory,
            NSUserDomainMask,
            true
        )
        return paths.firstOrNull() as? String ?: ""
    }
    
    /**
     * Get width of an image
     */
    @ExperimentalForeignApi
    fun getImageWidth(image: Any?): Int {
        return if (image is UIImage) {
            // Use our extension function to get width from CGSize
            image.size.getWidth().toInt()
        } else {
            0
        }
    }

    /**
     * Get height of an image
     */
    @ExperimentalForeignApi
    fun getImageHeight(image: Any?): Int {
        return if (image is UIImage) {
            // Use our extension function to get height from CGSize
            image.size.getHeight().toInt()
        } else {
            0
        }
    }
}