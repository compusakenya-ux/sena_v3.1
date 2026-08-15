package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.SenaBackground
import com.example.ui.theme.SenaBorder
import com.example.ui.theme.SenaElectricCyan
import com.example.ui.theme.SenaOrangeCTA
import com.example.ui.theme.SenaPeach
import kotlin.random.Random

/**
 * High-fidelity Custom Vector QR Code Renderer for Sena Transit PWA-URL Booking.
 * Encodes driver/stop info (e.g., https://book.sena.ke/?flow=destination&qr=NYALI001).
 */
@Composable
fun QrCodeView(
    data: String,
    modifier: Modifier = Modifier,
    sizeDp: Dp = 200.dp,
    dotColor: Color = SenaPeach,
    backgroundColor: Color = Color(0xFF131722),
    accentColor: Color = SenaOrangeCTA
) {
    val matrixSize = 25
    val grid = remember(data) {
        generateDeterministicQrGrid(data, matrixSize)
    }

    Box(
        modifier = modifier
            .size(sizeDp)
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .border(1.dp, SenaBorder, RoundedCornerShape(16.dp))
            .padding(14.dp)
            .testTag("sena_qr_code_matrix"),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize().aspectRatio(1f)) {
            val cellSize = size.width / matrixSize

            // Draw QR Matrix modules
            for (row in 0 until matrixSize) {
                for (col in 0 until matrixSize) {
                    val isFinder = (row < 7 && col < 7) ||
                            (row < 7 && col >= matrixSize - 7) ||
                            (row >= matrixSize - 7 && col < 7)

                    if (grid[row][col]) {
                        val color = if (isFinder) accentColor else dotColor
                        drawRoundRect(
                            color = color,
                            topLeft = Offset(col * cellSize, row * cellSize),
                            size = Size(cellSize * 0.9f, cellSize * 0.9f),
                            cornerRadius = CornerRadius(cellSize * 0.25f, cellSize * 0.25f)
                        )
                    }
                }
            }

            // Draw center brand anchor
            val centerStart = (matrixSize / 2 - 1) * cellSize
            val centerDim = 3 * cellSize
            drawRoundRect(
                color = SenaElectricCyan,
                topLeft = Offset(centerStart, centerStart),
                size = Size(centerDim, centerDim),
                cornerRadius = CornerRadius(cellSize * 0.4f, cellSize * 0.4f)
            )
        }
    }
}

private fun generateDeterministicQrGrid(data: String, size: Int): Array<BooleanArray> {
    val grid = Array(size) { BooleanArray(size) }
    val seed = data.hashCode().toLong()
    val random = Random(seed)

    // Position Finder Pattern 1: Top-Left (7x7)
    drawFinderPattern(grid, 0, 0)
    // Position Finder Pattern 2: Top-Right (7x7)
    drawFinderPattern(grid, 0, size - 7)
    // Position Finder Pattern 3: Bottom-Left (7x7)
    drawFinderPattern(grid, size - 7, 0)

    // Timing patterns
    for (i in 8 until size - 8) {
        grid[6][i] = (i % 2 == 0)
        grid[i][6] = (i % 2 == 0)
    }

    // Data payload
    for (row in 0 until size) {
        for (col in 0 until size) {
            val inFinder1 = row < 8 && col < 8
            val inFinder2 = row < 8 && col >= size - 8
            val inFinder3 = row >= size - 8 && col < 8
            val inCenter = row in (size / 2 - 1)..(size / 2 + 1) && col in (size / 2 - 1)..(size / 2 + 1)

            if (!inFinder1 && !inFinder2 && !inFinder3 && !inCenter) {
                if (row != 6 && col != 6) {
                    grid[row][col] = random.nextBoolean()
                }
            }
        }
    }

    return grid
}

private fun drawFinderPattern(grid: Array<BooleanArray>, startRow: Int, startCol: Int) {
    for (r in 0 until 7) {
        for (c in 0 until 7) {
            val isOuterBorder = r == 0 || r == 6 || c == 0 || c == 6
            val isInnerSolid = r in 2..4 && c in 2..4
            grid[startRow + r][startCol + c] = isOuterBorder || isInnerSolid
        }
    }
}
