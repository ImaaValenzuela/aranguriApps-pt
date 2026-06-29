package com.mitimiti.app

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

expect fun ByteArray.compressImage(): ByteArray

expect fun ByteArray.toImageBitmap(): androidx.compose.ui.graphics.ImageBitmap

expect fun ByteArray.toFirebaseData(): dev.gitlive.firebase.storage.Data

expect fun dev.gitlive.firebase.storage.Data.toByteArray(): ByteArray

expect suspend fun dev.gitlive.firebase.storage.StorageReference.downloadBytes(maxSize: Long): ByteArray
