package com.mitimiti.app.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import qrcode.raw.QRCodeProcessor

@Composable
fun QRCodeView(
    text: String,
    modifier: Modifier = Modifier,
    darkColor: Color = Color.Black,
    brightColor: Color = Color.White,
) {
    val qrCodeRawData =
        remember(text) {
            try {
                QRCodeProcessor(text).encode()
            } catch (e: Exception) {
                null
            }
        }

    if (qrCodeRawData != null) {
        Canvas(modifier = modifier) {
            val sizeInCells = qrCodeRawData.size
            if (sizeInCells > 0) {
                val cellSizeX = size.width / sizeInCells
                val cellSizeY = size.height / sizeInCells

                // Draw background
                drawRect(
                    color = brightColor,
                    size = size,
                )

                for (row in 0 until sizeInCells) {
                    for (col in 0 until sizeInCells) {
                        val cell = qrCodeRawData[row][col]
                        if (cell.dark) {
                            drawRect(
                                color = darkColor,
                                topLeft = Offset(col * cellSizeX, row * cellSizeY),
                                size = Size(cellSizeX + 0.5f, cellSizeY + 0.5f),
                            )
                        }
                    }
                }
            }
        }
    }
}
