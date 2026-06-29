package com.mitimiti.app

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts

@Composable
actual fun rememberTextSharer(): (String) -> Unit {
    val context = LocalContext.current
    return remember(context) {
        { text ->
            val sendIntent: Intent =
                Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, text)
                    type = "text/plain"
                }
            val shareIntent = Intent.createChooser(sendIntent, null)
            context.startActivity(shareIntent)
        }
    }
}

@Composable
actual fun rememberQRScanner(onScanResult: (String) -> Unit): () -> Unit {
    val context = LocalContext.current
    val scanner =
        remember(context) {
            val options =
                GmsBarcodeScannerOptions.Builder()
                    .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                    .build()
            GmsBarcodeScanning.getClient(context, options)
        }
    return remember(scanner) {
        {
            scanner.startScan()
                .addOnSuccessListener { barcode ->
                    barcode.rawValue?.let { onScanResult(it) }
                }
                .addOnFailureListener {
                    // Fail silently or log
                }
        }
    }
}

@Composable
actual fun rememberImagePicker(onImagePicked: (ByteArray) -> Unit): () -> Unit {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            try {
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    val bytes = inputStream.readBytes()
                    onImagePicked(bytes)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    return remember {
        { launcher.launch("image/*") }
    }
}
