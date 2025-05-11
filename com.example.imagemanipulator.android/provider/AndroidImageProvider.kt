package com.example.imagemanipulator.android.provider

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import com.example.imagemanipulator.android.util.ImageHelper
import com.example.imagemanipulator.shared.transformation.ImageTransformation
import com.example.imagemanipulator.shared.transformation.Point
import com.example.imagemanipulator.shared.ui.PlatformImageProvider
import com.example.imagemanipulator.shared.ui.TextOverlay
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

/**
 * Android-specific implementation of PlatformImageProvider.
 * This class handles image loading and transformation for Android.
 */
class AndroidImageProvider(
    private val context: Context,
    private val coroutineScope: CoroutineScope
) : PlatformImageProvider {
    
    // Image helper for loading images
    private val imageHelper = ImageHelper(context)
    
    // Transformation manager
    private val transformation = ImageTransformation()
    
    // Image state
    private val _bitmap = mutableStateOf<Bitmap?>(null)
    private val _imageBitmap = mutableStateOf<ImageBitmap?>(null)
    override val currentImageBitmap: State<ImageBitmap?> = _imageBitmap
    
    // Image picker launcher
    private var _imagePicker: ActivityResultLauncher<String>? = null
    
    // Set up the image picker
    fun setImagePicker(launcher: ActivityResultLauncher<String>) {
        _imagePicker = launcher
    }
    
    // Observable transformation states
    private val _scale = mutableStateOf(transformation.scale)
    override val scale: State<Float> = _scale
    
    private val _rotation = mutableStateOf(transformation.rotation)
    override val rotation: State<Float> = _rotation
    
    private val _offset = mutableStateOf(transformation.offset)
    override val offset: State<Point> = _offset
    
    // Text overlays
    private val _textOverlays = mutableStateOf<List<TextOverlay>>(emptyList())
    override val textOverlays: State<List<TextOverlay>> = _textOverlays
    
    // Process a selected image URI
    fun processImageUri(uri: Uri?) {
        uri?.let {
            coroutineScope.launch {
                val bitmap = imageHelper.loadImage(uri.toString()) as? Bitmap
                _bitmap.value = bitmap
                _imageBitmap.value = bitmap?.asImageBitmap()
                
                // Reset transformations when loading a new image
                resetTransformations()
            }
        }
    }
    
    // Launch the image picker
    override fun pickImage() {
        _imagePicker?.launch("image/*")
    }
    
    // Apply scaling
    override fun applyScale(scale: Float) {
        transformation.applyScale(scale)
        _scale.value = transformation.scale
    }
    
    // Apply rotation
    override fun applyRotation(rotation: Float) {
        transformation.applyRotation(rotation)
        _rotation.value = transformation.rotation
    }
    
    // Apply translation
    override fun applyTranslation(dx: Float, dy: Float) {
        transformation.applyTranslation(dx, dy)
        _offset.value = transformation.offset
    }
    
    // Set scale directly
    override fun setScale(scale: Float) {
        transformation.setScale(scale)
        _scale.value = transformation.scale
    }
    
    // Set rotation directly
    override fun setRotation(rotation: Float) {
        transformation.setRotation(rotation)
        _rotation.value = transformation.rotation
    }
    
    // Reset all transformations
    override fun resetTransformations() {
        transformation.reset()
        _scale.value = transformation.scale
        _rotation.value = transformation.rotation
        _offset.value = transformation.offset
    }
    
    // Add a text overlay
    override fun addTextOverlay(text: String, position: Point) {
        // Create new text overlay
        val textOverlay = TextOverlay(
            text = text,
            position = position
        )
        
        // Add to list
        val updatedList = _textOverlays.value.toMutableList()
        updatedList.add(textOverlay)
        _textOverlays.value = updatedList
    }
    
    // Remove a text overlay
    override fun removeTextOverlay(id: Long) {
        val updatedList = _textOverlays.value.filter { it.id != id }
        _textOverlays.value = updatedList
    }
    
    // Export current bitmap to base64 string
    override fun exportToBase64(callback: (String?) -> Unit) {
        coroutineScope.launch {
            val base64 = withContext(Dispatchers.Default) {
                _bitmap.value?.let { bitmap ->
                    try {
                        // Create a new bitmap with the current transformations applied
                        // (For a real implementation, we would render the bitmap with transformations)
                        // This is a simplified version that just exports the original bitmap
                        val outputStream = ByteArrayOutputStream()
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                        val byteArray = outputStream.toByteArray()
                        Base64.encodeToString(byteArray, Base64.DEFAULT)
                    } catch (e: Exception) {
                        null
                    }
                }
            }
            callback(base64)
        }
    }
}

/**
 * Composable helper function to create and remember an AndroidImageProvider
 */
@Composable
fun rememberAndroidImageProvider(): AndroidImageProvider {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    val imageProvider = remember { AndroidImageProvider(context, coroutineScope) }
    
    // Create and set up the image picker
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageProvider.processImageUri(uri)
    }
    
    // Set the image picker
    DisposableEffect(imageProvider) {
        imageProvider.setImagePicker(imagePicker)
        onDispose { }
    }
    
    return imageProvider
}