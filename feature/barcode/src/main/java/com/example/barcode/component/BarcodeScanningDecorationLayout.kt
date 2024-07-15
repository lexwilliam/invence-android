package com.example.barcode.component

import android.graphics.PointF
import android.graphics.RectF
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toRectF
import com.example.barcode.model.ScanningResult
import com.lexwilliam.core_ui.theme.InvenceTheme
import com.lexwilliam.core_ui.util.dp2Px

@Composable
fun BarcodeScanningDecorationLayout(
    width: Int,
    height: Int,
    onScanningAreaReady: (RectF) -> Unit,
    scanningResult: ScanningResult
) {
    fun calculateScanningRect(
        size: Int,
        centerPoint: PointF
    ): RectF {
        val scanningAreaSize = size * 0.8F
        val left = centerPoint.x - scanningAreaSize * 0.5F
        val top = centerPoint.y - scanningAreaSize * 0.5F
        val right = centerPoint.x + scanningAreaSize * 0.5F
        val bottom = centerPoint.y + scanningAreaSize * 0.1F
        return RectF(left, top, right, bottom)
    }

    fun calculateInstructionTextRect(
        paint: android.graphics.Paint,
        text: String
    ): RectF {
        return runCatching {
            val rect: android.graphics.Rect = android.graphics.Rect()
            paint.getTextBounds(text, 0, text.length, rect)
            rect.toRectF()
        }.getOrNull() ?: RectF()
    }

    val scanningAreaPath: Path by remember { mutableStateOf(Path()) }
    val cameraBoundaryPath: Path by remember { mutableStateOf(Path()) }
    val barcodeBoundaryPath: Path by remember { mutableStateOf(Path()) }
    val instructionText = stringResource(id = scanningResult.instructionResId)
    val instructionTextPaint =
        Paint().asFrameworkPaint().apply {
            isAntiAlias = true
            textSize = LocalDensity.current.run { 18.sp.toPx() }
            color = InvenceTheme.colors.neutral10.toArgb()
        }

    val centerPoint = PointF(width * 0.5F, height * 0.5F)
    val scanningAreaRect = calculateScanningRect(size = minOf(width, height), centerPoint)
    val scanningFrameStrokeSize = dp2Px(dp = 4.dp)
    val scanningFrameCornerRadius = dp2Px(dp = 6.dp)
    val scanningFrameStrokeColor = InvenceTheme.colors.secondary
    val barcodeResultBoundaryStrokeSize = dp2Px(dp = 4.dp)

    Canvas(
        modifier =
            Modifier
                .fillMaxSize()
                .onGloballyPositioned {
                    onScanningAreaReady.invoke(scanningAreaRect)
                },
        onDraw = {
            scanningAreaPath.reset()
            cameraBoundaryPath.reset()
            barcodeBoundaryPath.reset()

            // draw the area outside of scanning rectangle
            scanningAreaPath.addRect(
                Rect(
                    left = scanningAreaRect.left,
                    top = scanningAreaRect.top,
                    right = scanningAreaRect.right,
                    bottom = scanningAreaRect.bottom
                )
            )
            cameraBoundaryPath.addRect(Rect(Offset.Zero, Offset(width.toFloat(), height.toFloat())))
            drawPath(
                path =
                    Path.combine(
                        operation = PathOperation.Xor,
                        scanningAreaPath,
                        cameraBoundaryPath
                    ),
                color = Color.Black,
                alpha = 0.5F
            )

            // draw the scanning area frame
            drawRoundRect(
                color = Color.Gray,
                topLeft = Offset(x = scanningAreaRect.left, y = scanningAreaRect.top),
                size = Size(width = scanningAreaRect.width(), height = scanningAreaRect.height()),
                alpha = 0.8F,
                style =
                    Stroke(
                        join = StrokeJoin.Round,
                        width = scanningFrameStrokeSize
                    ),
                cornerRadius =
                    CornerRadius(
                        x = scanningFrameCornerRadius,
                        y = scanningFrameCornerRadius
                    )
            )

            // draw the instruction text
            calculateInstructionTextRect(instructionTextPaint, instructionText).let {
                    textBoundary ->
                drawIntoCanvas {
                    it.nativeCanvas.drawText(
                        instructionText,
                        centerPoint.x - textBoundary.width() * 0.5F,
                        (scanningAreaRect.top - textBoundary.height()) * 0.5F,
                        instructionTextPaint
                    )
                }
            }

            // draw the bar code result boundary
            if (scanningResult.barCodeResult != null) {
                scanningResult.barCodeResult?.globalPosition?.let {
                    barcodeBoundaryPath.moveTo(x = it.right - it.width() * 0.2F, y = it.top)
                    barcodeBoundaryPath.lineTo(x = it.right, y = it.top)

                    barcodeBoundaryPath.lineTo(x = it.right, y = it.bottom)
                    barcodeBoundaryPath.lineTo(x = it.right - it.width() * 0.2F, y = it.bottom)

                    barcodeBoundaryPath.moveTo(x = it.left + it.width() * 0.2F, y = it.bottom)
                    barcodeBoundaryPath.lineTo(x = it.left, y = it.bottom)
                    barcodeBoundaryPath.lineTo(x = it.left, y = it.top)
                    barcodeBoundaryPath.lineTo(x = it.left + it.width() * 0.2F, y = it.top)

                    drawPath(
                        path = barcodeBoundaryPath,
                        color = scanningFrameStrokeColor,
                        style =
                            Stroke(
                                join = StrokeJoin.Bevel,
                                width = barcodeResultBoundaryStrokeSize
                            )
                    )
                }
            }
        }
    )
}