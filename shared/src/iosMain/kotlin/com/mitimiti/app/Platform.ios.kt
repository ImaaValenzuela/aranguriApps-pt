package com.mitimiti.app

import platform.UIKit.UIDevice
import androidx.compose.ui.graphics.toComposeImageBitmap
import dev.gitlive.firebase.storage.Data
import platform.Foundation.NSData
import platform.Foundation.dataWithBytes
import platform.Foundation.dataWithContentsOfURL
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import kotlinx.cinterop.useContents
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class IOSPlatform : Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}

actual fun getPlatform(): Platform = IOSPlatform()

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
actual fun ByteArray.compressImage(): ByteArray = this.usePinned { pinned ->
    val nsData = NSData.dataWithBytes(bytes = pinned.addressOf(0), length = this.size.toULong())
    val uiImage = platform.UIKit.UIImage.imageWithData(nsData) ?: return@usePinned this
    
    val maxSize = 512.0
    val width = uiImage.size.useContents { width }
    val height = uiImage.size.useContents { height }
    
    if (width <= maxSize && height <= maxSize) {
        val compressedData = platform.UIKit.UIImageJPEGRepresentation(uiImage, 0.8) ?: return@usePinned this
        val byteArray = ByteArray(compressedData.length.toInt())
        val bytesPointer = compressedData.bytes
        if (compressedData.length > 0u && bytesPointer != null) {
            byteArray.usePinned { resultPinned ->
                platform.posix.memcpy(resultPinned.addressOf(0), bytesPointer, compressedData.length)
            }
        }
        return@usePinned byteArray
    }
    
    val ratio = width / height
    val newWidth = if (width > height) maxSize else maxSize * ratio
    val newHeight = if (height > width) maxSize else maxSize / ratio
    
    val newSize = platform.CoreGraphics.CGSizeMake(newWidth, newHeight)
    platform.UIKit.UIGraphicsBeginImageContextWithOptions(newSize, false, 1.0)
    uiImage.drawInRect(platform.CoreGraphics.CGRectMake(0.0, 0.0, newWidth, newHeight))
    val scaledImage = platform.UIKit.UIGraphicsGetImageFromCurrentImageContext()
    platform.UIKit.UIGraphicsEndImageContext()
    
    if (scaledImage == null) return@usePinned this
    
    val compressedData = platform.UIKit.UIImageJPEGRepresentation(scaledImage, 0.8) ?: return@usePinned this
    val byteArray = ByteArray(compressedData.length.toInt())
    val bytesPointer = compressedData.bytes
    if (compressedData.length > 0u && bytesPointer != null) {
        byteArray.usePinned { resultPinned ->
            platform.posix.memcpy(resultPinned.addressOf(0), bytesPointer, compressedData.length)
        }
    }
    return@usePinned byteArray
}

actual fun ByteArray.toImageBitmap(): androidx.compose.ui.graphics.ImageBitmap {
    return org.jetbrains.skia.Image.makeFromEncoded(this).toComposeImageBitmap()
}

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
@Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")
actual fun ByteArray.toFirebaseData(): Data = this.usePinned { pinned ->
    val nsData = NSData.dataWithBytes(bytes = pinned.addressOf(0), length = this.size.toULong())
    Data(nsData)
}

@OptIn(ExperimentalForeignApi::class)
@Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")
actual fun Data.toByteArray(): ByteArray {
    val nsData = this.data
    val byteArray = ByteArray(nsData.length.toInt())
    val bytesPointer = nsData.bytes
    if (nsData.length > 0u && bytesPointer != null) {
        byteArray.usePinned { pinned ->
            platform.posix.memcpy(pinned.addressOf(0), bytesPointer, nsData.length)
        }
    }
    return byteArray
}

@OptIn(ExperimentalForeignApi::class)
actual suspend fun dev.gitlive.firebase.storage.StorageReference.downloadBytes(maxSize: Long): ByteArray =
    kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Default) {
        val url = getDownloadUrl()
        val nsUrl = platform.Foundation.NSURL(string = url)
        val data = platform.Foundation.NSData.dataWithContentsOfURL(url = nsUrl)
            ?: throw Exception("Failed to download data from URL: $url")
        val byteArray = ByteArray(data.length.toInt())
        val bytesPointer = data.bytes

        if (data.length > 0u && bytesPointer != null) {
            byteArray.usePinned { pinned ->
                platform.posix.memcpy(pinned.addressOf(0), bytesPointer, data.length)
            }
        }
        byteArray
    }


