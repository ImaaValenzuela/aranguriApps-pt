@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package com.mitimiti.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.mitimiti.app.presentation.consumo.TicketParser
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.allocArrayOf
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.useContents
import kotlinx.cinterop.usePinned
import platform.AVFoundation.AVCaptureConnection
import platform.AVFoundation.AVCaptureDevice
import platform.AVFoundation.AVCaptureDeviceInput
import platform.AVFoundation.AVCaptureMetadataOutput
import platform.AVFoundation.AVCaptureMetadataOutputObjectsDelegateProtocol
import platform.AVFoundation.AVCaptureOutput
import platform.AVFoundation.AVCaptureSession
import platform.AVFoundation.AVCaptureVideoDataOutput
import platform.AVFoundation.AVCaptureVideoDataOutputSampleBufferDelegateProtocol
import platform.AVFoundation.AVCaptureVideoPreviewLayer
import platform.AVFoundation.AVLayerVideoGravityResizeAspectFill
import platform.AVFoundation.AVMediaTypeVideo
import platform.AVFoundation.AVMetadataMachineReadableCodeObject
import platform.AVFoundation.AVMetadataObjectTypeQRCode
import platform.CoreMedia.CMSampleBufferRef
import platform.Foundation.NSData
import platform.Foundation.create
import platform.UIKit.NSTextAlignmentCenter
import platform.UIKit.NSTextAlignmentLeft
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIColor
import platform.UIKit.UIEvent
import platform.UIKit.UIFont
import platform.UIKit.UIImage
import platform.UIKit.UILabel
import platform.UIKit.UITouch
import platform.UIKit.UIView
import platform.UIKit.UIViewController
import platform.UIKit.popoverPresentationController
import platform.Vision.VNImageRequestHandler
import platform.Vision.VNRecognizeTextRequest
import platform.Vision.VNRecognizedText
import platform.Vision.VNRecognizedTextObservation
import platform.Vision.VNRequestTextRecognitionLevelAccurate
import platform.darwin.DISPATCH_QUEUE_PRIORITY_DEFAULT
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_global_queue
import platform.darwin.dispatch_get_main_queue

@Composable
actual fun rememberTextSharer(): (String) -> Unit {
    return remember {
        { text ->
            val rootViewController = UIApplication.sharedApplication.keyWindow?.rootViewController
            val activityViewController =
                UIActivityViewController(
                    activityItems = listOf(text),
                    applicationActivities = null,
                )

            // For iPad compatibility
            activityViewController.popoverPresentationController?.sourceView = rootViewController?.view

            rootViewController?.presentViewController(
                activityViewController,
                animated = true,
                completion = null,
            )
        }
    }
}

@Composable
actual fun rememberQRScanner(onScanResult: (String) -> Unit): () -> Unit {
    return remember {
        {
            val rootViewController = UIApplication.sharedApplication.keyWindow?.rootViewController
            if (rootViewController != null) {
                val scannerVC = QRScannerViewController(onScanResult)
                rootViewController.presentViewController(scannerVC, animated = true, completion = null)
            }
        }
    }
}

private class QRScannerViewController(val onScan: (String) -> Unit) :
    UIViewController(
        null,
        null,
    ),
    AVCaptureMetadataOutputObjectsDelegateProtocol {
    private var captureSession: AVCaptureSession? = null
    private var previewLayer: AVCaptureVideoPreviewLayer? = null

    override fun viewDidLoad() {
        super.viewDidLoad()

        view.backgroundColor = UIColor.blackColor

        val captureSession = AVCaptureSession()
        this.captureSession = captureSession

        val videoCaptureDevice = AVCaptureDevice.defaultDeviceWithMediaType(AVMediaTypeVideo) ?: return

        val videoInput =
            try {
                AVCaptureDeviceInput.deviceInputWithDevice(videoCaptureDevice, null) as? AVCaptureDeviceInput
            } catch (e: Exception) {
                null
            } ?: return

        if (captureSession.canAddInput(videoInput)) {
            captureSession.addInput(videoInput)
        } else {
            return
        }

        val metadataOutput = AVCaptureMetadataOutput()
        if (captureSession.canAddOutput(metadataOutput)) {
            captureSession.addOutput(metadataOutput)
            metadataOutput.setMetadataObjectsDelegate(this, platform.darwin.dispatch_get_main_queue())
            metadataOutput.setMetadataObjectTypes(listOf(AVMetadataObjectTypeQRCode))
        } else {
            return
        }

        val previewLayer = AVCaptureVideoPreviewLayer.layerWithSession(captureSession) as AVCaptureVideoPreviewLayer
        previewLayer.setFrame(view.layer.bounds)
        previewLayer.videoGravity = AVLayerVideoGravityResizeAspectFill
        view.layer.addSublayer(previewLayer)
        this.previewLayer = previewLayer

        // Close Hint Label
        val closeLabel =
            UILabel().apply {
                text = "Toca la pantalla para volver"
                textColor = UIColor.whiteColor
                textAlignment = NSTextAlignmentCenter
                font = UIFont.boldSystemFontOfSize(16.0)
                setFrame(
                    view.bounds.useContents {
                        platform.CoreGraphics.CGRectMake(20.0, 40.0, size.width - 40.0, 44.0)
                    },
                )
            }
        view.addSubview(closeLabel)

        // Guide / Scan Box
        val scanBox =
            UIView().apply {
                setFrame(platform.CoreGraphics.CGRectMake(0.0, 0.0, 240.0, 240.0))
                setCenter(view.center)
                layer.borderWidth = 3.0
                layer.borderColor = UIColor.greenColor.CGColor
                layer.cornerRadius = 12.0
            }
        view.addSubview(scanBox)

        // Guide Label
        val label =
            UILabel().apply {
                text = "Alineá el código QR dentro del recuadro"
                textColor = UIColor.whiteColor
                textAlignment = NSTextAlignmentCenter
                font = UIFont.systemFontOfSize(14.0)
                setFrame(
                    view.bounds.useContents {
                        platform.CoreGraphics.CGRectMake(20.0, size.height - 100.0, size.width - 40.0, 40.0)
                    },
                )
            }
        view.addSubview(label)

        captureSession.startRunning()
    }

    fun closeScanner() {
        captureSession?.stopRunning()
        dismissViewControllerAnimated(true, completion = null)
    }

    override fun touchesBegan(
        touches: Set<*>,
        withEvent: UIEvent?,
    ) {
        super.touchesBegan(touches, withEvent)
        closeScanner()
    }

    override fun captureOutput(
        output: AVCaptureOutput,
        didOutputMetadataObjects: List<*>,
        fromConnection: AVCaptureConnection,
    ) {
        val metadataObject = didOutputMetadataObjects.firstOrNull() as? AVMetadataMachineReadableCodeObject
        if (metadataObject?.type == AVMetadataObjectTypeQRCode) {
            val scannedValue = metadataObject?.stringValue
            if (scannedValue != null) {
                platform.darwin.dispatch_async(platform.darwin.dispatch_get_main_queue()) {
                    closeScanner()
                    onScan(scannedValue)
                }
            }
        }
    }

    override fun viewWillDisappear(animated: Boolean) {
        super.viewWillDisappear(animated)
        captureSession?.stopRunning()
    }
}

@OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
@Composable
actual fun rememberImagePicker(onImagePicked: (ByteArray) -> Unit): () -> Unit {
    val delegate =
        remember {
            object :
                platform.darwin.NSObject(),
                platform.UIKit.UIImagePickerControllerDelegateProtocol,
                platform.UIKit.UINavigationControllerDelegateProtocol {
                override fun imagePickerController(
                    picker: platform.UIKit.UIImagePickerController,
                    didFinishPickingMediaWithInfo: Map<Any?, *>,
                ) {
                    val image =
                        didFinishPickingMediaWithInfo[platform.UIKit.UIImagePickerControllerOriginalImage]
                            as? platform.UIKit.UIImage
                    if (image != null) {
                        val data = platform.UIKit.UIImageJPEGRepresentation(image, 0.8)
                        if (data != null) {
                            val bytes = ByteArray(data.length.toInt())
                            val pointer = data.bytes
                            if (pointer != null) {
                                bytes.usePinned { pinned ->
                                    platform.posix.memcpy(pinned.addressOf(0), pointer, data.length)
                                }
                                onImagePicked(bytes)
                            }
                        }
                    }
                    picker.dismissViewControllerAnimated(true, completion = null)
                }

                override fun imagePickerControllerDidCancel(picker: platform.UIKit.UIImagePickerController) {
                    picker.dismissViewControllerAnimated(true, completion = null)
                }
            }
        }
    return remember {
        {
            val rootViewController = platform.UIKit.UIApplication.sharedApplication.keyWindow?.rootViewController
            if (rootViewController != null) {
                val picker =
                    platform.UIKit.UIImagePickerController().apply {
                        this.sourceType =
                            platform.UIKit.UIImagePickerControllerSourceType
                                .UIImagePickerControllerSourceTypePhotoLibrary
                        this.delegate = delegate
                    }
                rootViewController.presentViewController(picker, animated = true, completion = null)
            }
        }
    }
}

@Composable
actual fun rememberTextRecognizer(onResult: (String?) -> Unit): (ByteArray) -> Unit {
    return remember {
        { bytes ->
            val nsData =
                memScoped {
                    NSData.create(bytes = allocArrayOf(bytes), length = bytes.size.toULong())
                }
            val uiImage = UIImage.imageWithData(nsData)
            val cgImage = uiImage?.CGImage
            if (cgImage != null) {
                val request =
                    VNRecognizeTextRequest { request, error ->
                        if (error != null) {
                            dispatch_async(dispatch_get_main_queue()) {
                                onResult(null)
                            }
                            return@VNRecognizeTextRequest
                        }
                        val observations = request?.results as? List<*> ?: emptyList<Any?>()
                        val stringBuilder = StringBuilder()
                        for (obs in observations) {
                            val observation = obs as? VNRecognizedTextObservation ?: continue
                            val topCandidate = observation.topCandidates(1u).firstOrNull() as? VNRecognizedText
                            val text = topCandidate?.string
                            if (text != null) {
                                stringBuilder.append(text).append("\n")
                            }
                        }
                        dispatch_async(dispatch_get_main_queue()) {
                            onResult(stringBuilder.toString())
                        }
                    }
                request.recognitionLevel = VNRequestTextRecognitionLevelAccurate
                val handler = VNImageRequestHandler(cgImage, emptyMap<Any?, Any?>())
                val queue = dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT.toLong(), 0u)
                dispatch_async(queue) {
                    val success = handler.performRequests(listOf(request), null)
                    if (!success) {
                        dispatch_async(dispatch_get_main_queue()) {
                            onResult(null)
                        }
                    }
                }
            } else {
                onResult(null)
            }
        }
    }
}

@Composable
actual fun rememberTicketScanner(onResult: (String?) -> Unit): () -> Unit {
    return remember {
        {
            val rootViewController = UIApplication.sharedApplication.keyWindow?.rootViewController
            if (rootViewController != null) {
                val scannerVC = TicketScannerViewController(onResult)
                rootViewController.presentViewController(scannerVC, animated = true, completion = null)
            }
        }
    }
}

private class TicketScannerViewController(val onResult: (String?) -> Unit) :
    UIViewController(null, null),
    AVCaptureVideoDataOutputSampleBufferDelegateProtocol {
    private var captureSession: AVCaptureSession? = null
    private var previewLayer: AVCaptureVideoPreviewLayer? = null
    private var livePreviewLabel: UILabel? = null
    private var accumulatedRawText: String = ""
    private var lastProcessedTime: kotlin.time.TimeMark? = null

    override fun viewDidLoad() {
        super.viewDidLoad()
        view.backgroundColor = UIColor.blackColor

        val captureSession = AVCaptureSession()
        this.captureSession = captureSession

        val videoCaptureDevice = AVCaptureDevice.defaultDeviceWithMediaType(AVMediaTypeVideo) ?: return

        val videoInput =
            try {
                AVCaptureDeviceInput.deviceInputWithDevice(videoCaptureDevice, null) as? AVCaptureDeviceInput
            } catch (e: Exception) {
                null
            } ?: return

        if (captureSession.canAddInput(videoInput)) {
            captureSession.addInput(videoInput)
        } else {
            return
        }

        // Add VideoDataOutput for live frame parsing
        val videoDataOutput = AVCaptureVideoDataOutput()
        val queue = dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT.toLong(), 0u)
        videoDataOutput.setSampleBufferDelegate(this, queue)

        if (captureSession.canAddOutput(videoDataOutput)) {
            captureSession.addOutput(videoDataOutput)
        } else {
            return
        }

        val previewLayer = AVCaptureVideoPreviewLayer.layerWithSession(captureSession) as AVCaptureVideoPreviewLayer
        previewLayer.setFrame(view.layer.bounds)
        previewLayer.videoGravity = AVLayerVideoGravityResizeAspectFill
        view.layer.addSublayer(previewLayer)
        this.previewLayer = previewLayer

        // Header strip (top) — dark background so text is legible over camera
        val headerBg =
            UIView().apply {
                backgroundColor = UIColor.blackColor.colorWithAlphaComponent(0.55)
                setFrame(
                    view.bounds.useContents {
                        platform.CoreGraphics.CGRectMake(0.0, 0.0, size.width, 110.0)
                    },
                )
            }
        view.addSubview(headerBg)
        val titleLabel =
            UILabel().apply {
                text = "Escaneando ticket…"
                textColor = UIColor.whiteColor
                textAlignment = NSTextAlignmentCenter
                font = UIFont.boldSystemFontOfSize(20.0)
                setFrame(
                    view.bounds.useContents {
                        platform.CoreGraphics.CGRectMake(20.0, 52.0, size.width - 40.0, 28.0)
                    },
                )
            }
        val subtitleLabel =
            UILabel().apply {
                text = "Apuntá la cámara al ticket. Lee todo automáticamente."
                textColor = UIColor.whiteColor.colorWithAlphaComponent(0.80)
                textAlignment = NSTextAlignmentCenter
                font = UIFont.systemFontOfSize(12.0)
                setFrame(
                    view.bounds.useContents {
                        platform.CoreGraphics.CGRectMake(20.0, 82.0, size.width - 40.0, 18.0)
                    },
                )
            }
        headerBg.addSubview(titleLabel)
        headerBg.addSubview(subtitleLabel)

        // Scanned items card — sits above the buttons
        val previewBg =
            UIView().apply {
                backgroundColor = UIColor.colorWithRed(15 / 255.0, 23 / 255.0, 42 / 255.0, 0.92)
                layer.cornerRadius = 24.0
                layer.borderWidth = 1.5
                layer.borderColor = UIColor.colorWithRed(255 / 255.0, 255 / 255.0, 255 / 255.0, 0.15).CGColor
                setFrame(
                    view.bounds.useContents {
                        platform.CoreGraphics.CGRectMake(
                            16.0,
                            size.height - 290.0,
                            size.width - 32.0,
                            170.0,
                        )
                    },
                )
            }
        view.addSubview(previewBg)

        // Live list label
        val livePreviewLabel =
            UILabel().apply {
                text = "Enfocá el ticket para detectar productos..."
                textColor = UIColor.whiteColor
                textAlignment = NSTextAlignmentLeft
                numberOfLines = 0
                font = UIFont.systemFontOfSize(14.0)
                setFrame(
                    previewBg.bounds.useContents {
                        platform.CoreGraphics.CGRectMake(
                            16.0,
                            16.0,
                            size.width - 32.0,
                            148.0,
                        )
                    },
                )
            }
        previewBg.addSubview(livePreviewLabel)
        this.livePreviewLabel = livePreviewLabel

        // Cancel button
        val cancelView =
            UIView().apply {
                backgroundColor = UIColor.colorWithRed(51 / 255.0, 65 / 255.0, 85 / 255.0, 0.9)
                layer.cornerRadius = 20.0
                setFrame(
                    view.bounds.useContents {
                        val w = (size.width - 52.0) * 0.4
                        platform.CoreGraphics.CGRectMake(20.0, size.height - 100.0, w, 48.0)
                    },
                )
                val label =
                    UILabel().apply {
                        text = "Cancelar"
                        textColor = UIColor.whiteColor
                        textAlignment = NSTextAlignmentCenter
                        font = UIFont.boldSystemFontOfSize(15.0)
                        setFrame(
                            view.bounds.useContents {
                                val w = (size.width - 52.0) * 0.4
                                platform.CoreGraphics.CGRectMake(0.0, 0.0, w, 48.0)
                            },
                        )
                    }
                addSubview(label)
            }
        view.addSubview(cancelView)

        // Next button
        val nextView =
            UIView().apply {
                val celesteColor = UIColor.colorWithRed(116 / 255.0, 172 / 255.0, 223 / 255.0, 1.0)
                backgroundColor = celesteColor
                layer.cornerRadius = 20.0
                setFrame(
                    view.bounds.useContents {
                        val w = (size.width - 52.0) * 0.6
                        platform.CoreGraphics.CGRectMake(size.width - 20.0 - w, size.height - 100.0, w, 48.0)
                    },
                )
                val label =
                    UILabel().apply {
                        text = "Siguiente"
                        val darkCeleste = UIColor.colorWithRed(11 / 255.0, 37 / 255.0, 69 / 255.0, 1.0)
                        textColor = darkCeleste
                        textAlignment = NSTextAlignmentCenter
                        font = UIFont.boldSystemFontOfSize(15.0)
                        setFrame(
                            view.bounds.useContents {
                                val w = (size.width - 52.0) * 0.6
                                platform.CoreGraphics.CGRectMake(0.0, 0.0, w, 48.0)
                            },
                        )
                    }
                addSubview(label)
            }
        view.addSubview(nextView)

        captureSession.startRunning()
    }

    override fun captureOutput(
        output: AVCaptureOutput,
        didOutputSampleBuffer: CMSampleBufferRef?,
        fromConnection: AVCaptureConnection,
    ) {
        val now = kotlin.time.TimeSource.Monotonic.markNow()
        val lastTime = lastProcessedTime
        if (lastTime != null && now.elapsedNow().inWholeMilliseconds < 600) {
            return
        }
        lastProcessedTime = now

        if (didOutputSampleBuffer != null) {
            val request =
                VNRecognizeTextRequest { request, error ->
                    if (error != null) return@VNRecognizeTextRequest
                    val observations = request?.results as? List<*> ?: emptyList<Any?>()
                    val stringBuilder = StringBuilder()
                    for (obs in observations) {
                        val observation = obs as? VNRecognizedTextObservation ?: continue
                        val topCandidate = observation.topCandidates(1u).firstOrNull() as? VNRecognizedText
                        val text = topCandidate?.string
                        if (text != null) {
                            stringBuilder.append(text).append("\n")
                        }
                    }
                    val frameText = stringBuilder.toString()
                    dispatch_async(dispatch_get_main_queue()) {
                        // Accumulate text across frames — same as Google Lens approach
                        accumulatedRawText = "$accumulatedRawText\n$frameText"
                        updateLivePreview(accumulatedRawText)
                    }
                }
            request.recognitionLevel = VNRequestTextRecognitionLevelAccurate
            val handler = VNImageRequestHandler(didOutputSampleBuffer, emptyMap<Any?, Any?>())
            handler.performRequests(listOf(request), null)
        }
    }

    private fun updateLivePreview(accumulated: String) {
        val parsed = TicketParser.parse(accumulated)
        if (parsed.isNotEmpty()) {
            val total = parsed.sumOf { it.cost }
            val lines =
                parsed.take(8).joinToString("\n") {
                    "• ${it.name.take(28)}  $ ${".2f".let { f -> "%.$f".format(it.cost) }}"
                }
            val totalFmt = "%.2f".format(total)
            livePreviewLabel?.text =
                "${parsed.size} producto(s)  —  Total: \$$totalFmt\n\n$lines"
        } else {
            livePreviewLabel?.text =
                "Apuntá la cámara al ticket...\n\n(Asegurate de que se vean bien los precios)"
        }
    }

    override fun touchesBegan(
        touches: Set<*>,
        withEvent: UIEvent?,
    ) {
        val touch = touches.firstOrNull() as? UITouch ?: return
        val location = touch.locationInView(view)

        val width = view.bounds.useContents { size.width }
        val height = view.bounds.useContents { size.height }

        val touchX = location.useContents { x }
        val touchY = location.useContents { y }

        val cancelWidth = (width - 52.0) * 0.4
        val cancelMaxX = 20.0 + cancelWidth

        val nextWidth = (width - 52.0) * 0.6
        val nextMinX = width - 20.0 - nextWidth

        if (touchY > height - 100.0) {
            if (touchX < cancelMaxX) {
                // Cancel button area: bottom left
                captureSession?.stopRunning()
                onResult(null)
                dismissViewControllerAnimated(true, completion = null)
            } else if (touchX > nextMinX) {
                // Next button area: bottom right
                captureSession?.stopRunning()
                onResult(accumulatedRawText.ifBlank { null })
                dismissViewControllerAnimated(true, completion = null)
            }
        }
    }
}
