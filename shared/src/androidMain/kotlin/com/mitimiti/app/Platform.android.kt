package com.mitimiti.app

import android.os.Build
import androidx.compose.ui.graphics.asImageBitmap
import kotlinx.coroutines.tasks.await

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()

actual fun ByteArray.compressImage(): ByteArray {
    val bitmap = android.graphics.BitmapFactory.decodeByteArray(this, 0, this.size)
        ?: return this
    
    val maxSize = 512
    val width = bitmap.width
    val height = bitmap.height
    
    val scaledBitmap = if (width > maxSize || height > maxSize) {
        val ratio = width.toFloat() / height.toFloat()
        val newWidth = if (width > height) maxSize else (maxSize * ratio).toInt()
        val newHeight = if (height > width) maxSize else (maxSize / ratio).toInt()
        android.graphics.Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    } else {
        bitmap
    }
    
    val outputStream = java.io.ByteArrayOutputStream()
    scaledBitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 80, outputStream)
    val result = outputStream.toByteArray()
    
    if (scaledBitmap != bitmap) {
        scaledBitmap.recycle()
    }
    bitmap.recycle()
    
    return result
}

actual fun ByteArray.toImageBitmap(): androidx.compose.ui.graphics.ImageBitmap {
    val bitmap = android.graphics.BitmapFactory.decodeByteArray(this, 0, this.size)
        ?: throw IllegalArgumentException("Failed to decode image bytes")
    return bitmap.asImageBitmap()
}

actual fun ByteArray.toFirebaseData(): dev.gitlive.firebase.storage.Data {
    return dev.gitlive.firebase.storage.Data(this)
}

actual fun dev.gitlive.firebase.storage.Data.toByteArray(): ByteArray {
    return this.data
}

actual suspend fun dev.gitlive.firebase.storage.StorageReference.downloadBytes(maxSize: Long): ByteArray =
    kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
        val url = getDownloadUrl()
        val connection = java.net.URL(url).openConnection() as java.net.HttpURLConnection
        connection.connectTimeout = 10000
        connection.readTimeout = 10000
        connection.inputStream.use { it.readBytes() }
    }

