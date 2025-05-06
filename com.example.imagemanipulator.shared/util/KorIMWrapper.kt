package com.example.imagemanipulator.shared.util

import com.soywiz.korim.bitmap.Bitmap32
import com.soywiz.korim.format.ImageDecodingProps
import com.soywiz.korio.file.std.resourcesVfs
import com.soywiz.korim.format.ImageFormat
import com.soywiz.korim.format.PNG
import com.soywiz.korim.format.JPEG
import com.soywiz.korio.file.std.toVfs
import com.soywiz.korio.file.VfsFile
import com.soywiz.korio.file.baseName
import com.soywiz.korio.file.extensionLC

class KorIMWrapper {
    suspend fun load(path: String): Bitmap32? {
        return try {
            val file: VfsFile = if (path.startsWith("resources/")) {
                resourcesVfs[path.removePrefix("resources/")]
            } else {
                path.toVfs()
            }
            file.readBitmap32()
        } catch (e: Exception) {
            null
        }
    }
    suspend fun convert(image: Bitmap32, format: String): Bitmap32 {
        val imageFormat: ImageFormat = when (format.toLowerCase()) {
            "png" -> PNG
            "jpeg", "jpg" -> JPEG
            else -> return image // Return original image if format is not supported
        }
        return image.toBMP32().encode(imageFormat, ImageDecodingProps())
    }
}
