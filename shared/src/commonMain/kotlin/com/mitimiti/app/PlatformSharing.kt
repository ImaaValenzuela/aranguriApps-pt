package com.mitimiti.app

import androidx.compose.runtime.Composable

@Composable
expect fun rememberTextSharer(): (String) -> Unit

@Composable
expect fun rememberQRScanner(onScanResult: (String) -> Unit): () -> Unit
