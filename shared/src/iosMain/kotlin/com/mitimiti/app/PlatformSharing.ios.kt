@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package com.mitimiti.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.cinterop.useContents
import kotlinx.cinterop.usePinned
import kotlinx.cinterop.addressOf
import platform.AVFoundation.AVCaptureConnection
import platform.AVFoundation.AVCaptureDevice
import platform.AVFoundation.AVCaptureDeviceInput
import platform.AVFoundation.AVCaptureMetadataOutput
import platform.AVFoundation.AVCaptureMetadataOutputObjectsDelegateProtocol
import platform.AVFoundation.AVCaptureOutput
import platform.AVFoundation.AVCaptureSession
import platform.AVFoundation.AVCaptureVideoPreviewLayer
import platform.AVFoundation.AVLayerVideoGravityResizeAspectFill
import platform.AVFoundation.AVMediaTypeVideo
import platform.AVFoundation.AVMetadataMachineReadableCodeObject
import platform.AVFoundation.AVMetadataObjectTypeQRCode
import platform.UIKit.NSTextAlignmentCenter
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIColor
import platform.UIKit.UIEvent
import platform.UIKit.UIFont
import platform.UIKit.UILabel
import platform.UIKit.UIView
import platform.UIKit.UIViewController
import platform.UIKit.popoverPresentationController

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
    val delegate = remember {
        object : platform.darwin.NSObject(), platform.UIKit.UIImagePickerControllerDelegateProtocol, platform.UIKit.UINavigationControllerDelegateProtocol {
            override fun imagePickerController(
                picker: platform.UIKit.UIImagePickerController,
                didFinishPickingMediaWithInfo: Map<Any?, *>
            ) {
                val image = didFinishPickingMediaWithInfo[platform.UIKit.UIImagePickerControllerOriginalImage] as? platform.UIKit.UIImage
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
                val picker = platform.UIKit.UIImagePickerController().apply {
                    this.sourceType = platform.UIKit.UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypePhotoLibrary
                    this.delegate = delegate
                }
                rootViewController.presentViewController(picker, animated = true, completion = null)
            }
        }
    }
}

