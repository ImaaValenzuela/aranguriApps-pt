package com.mitimiti.app

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.mitimiti.app.presentation.consumo.ParsedExpenseItem
import com.mitimiti.app.presentation.consumo.TicketParser
import com.mitimiti.app.presentation.theme.ClayButton
import com.mitimiti.app.presentation.theme.MitiMitiTheme
import com.mitimiti.app.presentation.theme.claymorphic
import kotlin.math.max

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
    val launcher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent(),
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

@Composable
actual fun rememberTextRecognizer(onResult: (String?) -> Unit): (ByteArray) -> Unit {
    val recognizer =
        remember {
            TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        }
    return remember(recognizer) {
        { bytes ->
            try {
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                if (bitmap != null) {
                    val image = InputImage.fromBitmap(bitmap, 0)
                    recognizer.process(image)
                        .addOnSuccessListener { visionText ->
                            onResult(visionText.text)
                        }
                        .addOnFailureListener { e ->
                            e.printStackTrace()
                            onResult(null)
                        }
                } else {
                    onResult(null)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                onResult(null)
            }
        }
    }
}

/** A detected text line region in normalized screen coordinates (0..1). */
private data class TextHighlightRegion(
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float,
    val isExpenseItem: Boolean,
)

@Composable
actual fun rememberTicketScanner(onResult: (String?) -> Unit): () -> Unit {
    val context = LocalContext.current
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    var showCamera by remember { mutableStateOf(false) }

    var hasCameraPermission by remember {
        mutableStateOf(
            androidx.core.content.ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.CAMERA,
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED,
        )
    }

    val permissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
        ) { isGranted ->
            hasCameraPermission = isGranted
            if (!isGranted) {
                showCamera = false
            }
        }

    LaunchedEffect(showCamera) {
        if (showCamera && !hasCameraPermission) {
            permissionLauncher.launch(android.Manifest.permission.CAMERA)
        }
    }

    if (showCamera && hasCameraPermission) {
        // Raw text accumulated across frames (full-frame OCR, like Google Lens)
        var accumulatedRawText by remember { mutableStateOf("") }
        var accumulatedItems by remember { mutableStateOf<List<ParsedExpenseItem>>(emptyList()) }
        var textRegions by remember { mutableStateOf<List<TextHighlightRegion>>(emptyList()) }
        // Store image dimensions for coordinate mapping
        var imgBufW by remember { mutableStateOf(1) }
        var imgBufH by remember { mutableStateOf(1) }
        var imgRotation by remember { mutableStateOf(0) }

        Dialog(
            onDismissRequest = {
                showCamera = false
                onResult(null)
            },
            properties =
                androidx.compose.ui.window.DialogProperties(
                    usePlatformDefaultWidth = false,
                ),
        ) {
            MitiMitiTheme {
                val isDark = androidx.compose.foundation.isSystemInDarkTheme()
                val surfaceColor = MaterialTheme.colorScheme.surface
                val textColor = MaterialTheme.colorScheme.onBackground
                val primaryColor = MaterialTheme.colorScheme.primary

                Box(modifier = Modifier.fillMaxSize()) {
                    val cameraProviderFuture =
                        remember {
                            androidx.camera.lifecycle.ProcessCameraProvider.getInstance(context)
                        }

                    // Full-screen camera preview — no cropping box
                    AndroidView(
                        factory = { ctx ->
                            val previewView =
                                androidx.camera.view.PreviewView(ctx).apply {
                                    scaleType =
                                        androidx.camera.view.PreviewView.ScaleType.FILL_CENTER
                                }
                            val executor =
                                androidx.core.content.ContextCompat.getMainExecutor(ctx)
                            cameraProviderFuture.addListener({
                                val cameraProvider = cameraProviderFuture.get()
                                val preview =
                                    androidx.camera.core.Preview.Builder().build().also {
                                        it.setSurfaceProvider(previewView.surfaceProvider)
                                    }

                                val imageAnalysis =
                                    androidx.camera.core.ImageAnalysis.Builder()
                                        .setBackpressureStrategy(
                                            androidx.camera.core.ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST,
                                        )
                                        .build()

                                var lastAnalysisTime = 0L
                                val recognizer =
                                    TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

                                imageAnalysis.setAnalyzer(executor) { imageProxy ->
                                    val now = System.currentTimeMillis()
                                    if (now - lastAnalysisTime >= 600) {
                                        lastAnalysisTime = now
                                        @androidx.annotation.OptIn(
                                            androidx.camera.core.ExperimentalGetImage::class,
                                        )
                                        val mediaImage = imageProxy.image
                                        if (mediaImage != null) {
                                            val image =
                                                InputImage.fromMediaImage(
                                                    mediaImage,
                                                    imageProxy.imageInfo.rotationDegrees,
                                                )
                                            recognizer.process(image)
                                                .addOnSuccessListener { visionText ->
                                                    val rotation = imageProxy.imageInfo.rotationDegrees
                                                    val bufW = mediaImage.width
                                                    val bufH = mediaImage.height
                                                    imgBufW = bufW
                                                    imgBufH = bufH
                                                    imgRotation = rotation

                                                    val newText = visionText.text
                                                    if (newText.isNotBlank()) {
                                                        accumulatedRawText =
                                                            "$accumulatedRawText\n$newText"
                                                        accumulatedItems =
                                                            TicketParser.parse(accumulatedRawText)
                                                    }

                                                    // Build highlight regions from bounding boxes
                                                    // Display dimensions after rotation
                                                    val dispW = if (rotation == 90 || rotation == 270) bufH else bufW
                                                    val dispH = if (rotation == 90 || rotation == 270) bufW else bufH

                                                    val regions = mutableListOf<TextHighlightRegion>()
                                                    for (block in visionText.textBlocks) {
                                                        for (line in block.lines) {
                                                            val r = line.boundingBox ?: continue
                                                            // Map buffer rect → display rect based on rotation
                                                            val (dL, dT, dR, dB) =
                                                                when (rotation) {
                                                                    90 ->
                                                                        listOf(
                                                                            bufH - r.bottom,
                                                                            r.left,
                                                                            bufH - r.top,
                                                                            r.right,
                                                                        )
                                                                    270 ->
                                                                        listOf(
                                                                            r.top,
                                                                            bufW - r.right,
                                                                            r.bottom,
                                                                            bufW - r.left,
                                                                        )
                                                                    180 ->
                                                                        listOf(
                                                                            bufW - r.right,
                                                                            bufH - r.bottom,
                                                                            bufW - r.left,
                                                                            bufH - r.top,
                                                                        )
                                                                    else -> listOf(r.left, r.top, r.right, r.bottom)
                                                                }
                                                            val isItem =
                                                                TicketParser.parse(line.text).isNotEmpty()
                                                            regions.add(
                                                                TextHighlightRegion(
                                                                    left = dL.toFloat() / dispW,
                                                                    top = dT.toFloat() / dispH,
                                                                    right = dR.toFloat() / dispW,
                                                                    bottom = dB.toFloat() / dispH,
                                                                    isExpenseItem = isItem,
                                                                ),
                                                            )
                                                        }
                                                    }
                                                    textRegions = regions
                                                    imageProxy.close()
                                                }
                                                .addOnFailureListener {
                                                    imageProxy.close()
                                                }
                                        } else {
                                            imageProxy.close()
                                        }
                                    } else {
                                        imageProxy.close()
                                    }
                                }

                                val cameraSelector =
                                    androidx.camera.core.CameraSelector.DEFAULT_BACK_CAMERA
                                try {
                                    cameraProvider.unbindAll()
                                    cameraProvider.bindToLifecycle(
                                        lifecycleOwner,
                                        cameraSelector,
                                        preview,
                                        imageAnalysis,
                                    )
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }, executor)
                            previewView
                        },
                        modifier = Modifier.fillMaxSize(),
                    )

                    // Google Lens-style highlight overlay
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val sw = size.width
                        val sh = size.height
                        val dispW = if (imgRotation == 90 || imgRotation == 270) imgBufH else imgBufW
                        val dispH = if (imgRotation == 90 || imgRotation == 270) imgBufW else imgBufH
                        if (dispW == 0 || dispH == 0) return@Canvas

                        // FILL_CENTER scale + offset
                        val scale = max(sw / dispW.toFloat(), sh / dispH.toFloat())
                        val offX = (sw - dispW * scale) / 2f
                        val offY = (sh - dispH * scale) / 2f

                        for (region in textRegions) {
                            val sLeft = region.left * dispW * scale + offX
                            val sTop = region.top * dispH * scale + offY
                            val sRight = region.right * dispW * scale + offX
                            val sBottom = region.bottom * dispH * scale + offY
                            val color =
                                if (region.isExpenseItem) {
                                    primaryColor.copy(alpha = 0.38f)
                                } else {
                                    Color.White.copy(alpha = 0.13f)
                                }
                            drawRoundRect(
                                color = color,
                                topLeft = Offset(sLeft, sTop),
                                size = Size(sRight - sLeft, sBottom - sTop),
                                cornerRadius = CornerRadius(6.dp.toPx()),
                            )
                        }
                    }

                    // Top header strip — translucent
                    Column(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .align(Alignment.TopCenter)
                                .background(Color.Black.copy(alpha = 0.55f))
                                .padding(
                                    top = 44.dp,
                                    bottom = 14.dp,
                                    start = 20.dp,
                                    end = 20.dp,
                                ),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = "Escaneando ticket…",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "Apuntá la cámara al ticket. Lee todo automáticamente.",
                            color = Color.White.copy(alpha = 0.80f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                        )
                    }

                    // Bottom floating panel
                    Column(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 24.dp, start = 16.dp, end = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        // Results card
                        Column(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .claymorphic(
                                        backgroundColor = surfaceColor,
                                        cornerRadius = 28.dp,
                                        elevation = 8.dp,
                                        isDark = isDark,
                                    )
                                    .padding(horizontal = 18.dp, vertical = 14.dp),
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Text(
                                    text =
                                        if (accumulatedItems.isEmpty()) {
                                            "Leyendo ticket…"
                                        } else {
                                            "${accumulatedItems.size} producto(s) detectado(s)"
                                        },
                                    color = primaryColor,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                                if (accumulatedItems.isNotEmpty()) {
                                    Text(
                                        text = "$ ${"%.2f".format(accumulatedItems.sumOf { it.cost })}",
                                        color = textColor.copy(alpha = 0.75f),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            if (accumulatedItems.isEmpty()) {
                                Text(
                                    text = "Posicioná el ticket completo frente a la cámara…",
                                    color = textColor.copy(alpha = 0.50f),
                                    fontSize = 13.sp,
                                )
                            } else {
                                androidx.compose.foundation.lazy.LazyColumn(
                                    modifier = Modifier.heightIn(max = 170.dp),
                                    verticalArrangement = Arrangement.spacedBy(5.dp),
                                ) {
                                    items(accumulatedItems.size) { index ->
                                        val item = accumulatedItems[index]
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                        ) {
                                            Text(
                                                text = "• ${item.name}",
                                                color = textColor,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Medium,
                                                modifier = Modifier.weight(1f),
                                            )
                                            Text(
                                                text = "${"%.2f".format(item.cost)}",
                                                color = primaryColor,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            ClayButton(
                                onClick = {
                                    showCamera = false
                                    onResult(null)
                                },
                                backgroundColor = if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0),
                                contentColor = textColor,
                                cornerRadius = 20.dp,
                                modifier =
                                    Modifier
                                        .weight(1.4f)
                                        .height(50.dp),
                            ) {
                                Text(
                                    text = "Cancelar",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                )
                            }

                            ClayButton(
                                onClick = {
                                    showCamera = false
                                    onResult(accumulatedRawText.ifBlank { null })
                                },
                                backgroundColor = primaryColor,
                                contentColor = MaterialTheme.colorScheme.onPrimary,
                                cornerRadius = 20.dp,
                                modifier =
                                    Modifier
                                        .weight(2f)
                                        .height(50.dp),
                            ) {
                                Text(
                                    text =
                                        if (accumulatedItems.isEmpty()) {
                                            "Siguiente"
                                        } else {
                                            "Confirmar (${accumulatedItems.size})"
                                        },
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    return remember {
        {
            showCamera = true
        }
    }
}
