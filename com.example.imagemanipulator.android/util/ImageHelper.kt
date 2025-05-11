package com.example.imagemanipulator.android.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.example.imagemanipulator.shared.util.ImageHelperInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

/**
 * Android implementation of ImageHelperInterface
 */
class ImageHelper(private val context: Context) : ImageHelperInterface {
    
    override suspend fun loadImage(path: String): Any? = withContext(Dispatchers.IO) {
        try {
            val uri = Uri.parse(path)
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                return@withContext BitmapFactory.decodeStream(inputStream)
            }
            return@withContext null
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext null
        }
    }
    
    override suspend fun saveImage(image: Any?, path: String): Boolean = withContext(Dispatchers.IO) {
        if (image !is Bitmap) return@withContext false
        
        try {
            val file = File(path)
            val outputStream = FileOutputStream(file)
            
            // Save as PNG
            image.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.close()
            return@withContext true
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext false
        }
    }
    
    override fun getImageWidth(image: Any?): Int {
        return if (image is Bitmap) image.width else 0
    }
    
    override fun getImageHeight(image: Any?): Int {
        return if (image is Bitmap) image.height else 0
    }
}