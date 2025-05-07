package com.example.imagemanipulator.ios.util

import com.example.imagemanipulator.shared.util.KorIMWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import platform.UIKit.UIImage
import platform.UIKit.UIImagePNGRepresentation
import platform.Foundation.NSData
import platform.Foundation.NSURL
import platform.Foundation.writeToURL

actual class KorIMWrapper actual constructor(private val context: Any) {
    
    actual suspend fun load(path: String): Any? = withContext(Dispatchers.Default) {
        try {
            val url = NSURL.fileURLWithPath(path)
            val data = NSData.dataWithContentsOfURL(url) ?: return@withContext null
            val uiImage = UIImage.imageWithData(data) ?: return@withContext null
            return@withContext uiImage
        } catch (e: Exception) {
            println("Error loading image: ${e.message}")
            return@withContext null
        }
    }
    
    actual suspend fun save(image: Any?, path: String): Boolean = withContext(Dispatchers.Default) {
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
}