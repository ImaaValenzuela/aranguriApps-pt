package com.mitimiti.app

import androidx.compose.runtime.Composable

@Composable
expect fun rememberTextSharer(): (String) -> Unit

@Composable
expect fun rememberQRScanner(onScanResult: (String) -> Unit): () -> Unit

@Composable
expect fun rememberImagePicker(onImagePicked: (ByteArray) -> Unit): () -> Unit

@Composable
expect fun rememberTextRecognizer(onResult: (String?) -> Unit): (ByteArray) -> Unit

@Composable
expect fun rememberTicketScanner(onResult: (String?) -> Unit): () -> Unit
