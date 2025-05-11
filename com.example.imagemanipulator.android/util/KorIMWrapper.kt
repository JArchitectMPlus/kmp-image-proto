package com.example.imagemanipulator.android.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.example.imagemanipulator.shared.util.KorIMWrapper
import java.io.File
import java.io.FileOutputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Android implementation of KorIMWrapper
class KorIMWrapper(private val context: Any) {

    suspend fun load(path: String): Any? = withContext(Dispatchers.IO) {
        if (context !is Context) return@withContext null

        try {
            val uri = Uri.parse(path)
            val inputStream = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()
            return@withContext bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext null
        }
    }

    suspend fun save(image: Any?, path: String): Boolean = withContext(Dispatchers.IO) {
        if (image !is Bitmap) return@withContext false

        try {
            val file = File(path)

            // Create parent directories if they don't exist
            file.parentFile?.mkdirs()

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
}