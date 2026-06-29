package com.mitimiti.app

import androidx.compose.runtime.Composable

@Composable
expect fun rememberTextSharer(): (String) -> Unit

@Composable
expect fun rememberQRScanner(onScanResult: (String) -> Unit): () -> Unit

@Composable
expect fun rememberImagePicker(onImagePicked: (ByteArray) -> Unit): () -> Unit
