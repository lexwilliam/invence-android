package com.example.barcode.route

import android.Manifest
import android.graphics.PointF
import android.graphics.RectF
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.toRectF
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.barcode.extension.getCameraXProvider
import com.example.barcode.model.BarcodeScannerBottomSheetState
import com.example.barcode.model.ScanningResult
import com.example.barcode.navigation.BarcodeNavigationTarget
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.lexwilliam.barcode.R
import com.lexwilliam.core_ui.component.ObserveAsEvents
import com.lexwilliam.core_ui.theme.InvenceTheme
import com.lexwilliam.core_ui.util.dp2Px

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@androidx.camera.core.ExperimentalGetImage
@Composable
fun BarcodeRoute(
    viewModel: BarcodeViewModel = hiltViewModel(),
    onBackStack: () -> Unit,
    toProductDetail: (String) -> Unit,
    toProductForm: (String?, Boolean) -> Unit,
    onScreenLoaded: (Boolean) -> Unit = {},
    onPermissionGrantedResult: (Boolean) -> Unit = {}
) {
    val scanningResult by viewModel.scanningResult.collectAsStateWithLifecycle()
    val freezeCameraPreview by viewModel.freezeCameraPreview.collectAsStateWithLifecycle(false)
    val resultBottomSheetStateModel by
        viewModel.resultBottomSheetState.collectAsStateWithLifecycle()

    val imageAnalyzer = viewModel.getBarcodeImageAnalyzer()

    ObserveAsEvents(flow = viewModel.navigation) { target ->
        when (target) {
            BarcodeNavigationTarget.BackStack -> onBackStack()
            is BarcodeNavigationTarget.ProductDetail -> toProductDetail(target.productUUID)
            is BarcodeNavigationTarget.ProductForm ->
                toProductForm(
                    target.productUUID,
                    target.onlyID
                )
        }
    }

    val cameraPermissionState =
        rememberPermissionState(
            Manifest.permission.CAMERA,
            onPermissionResult = {
                onPermissionGrantedResult.invoke(it)
            }
        )

    val resultBottomSheetState =
        rememberModalBottomSheetState(
            skipPartiallyExpanded = true,
            confirmValueChange = {
                it != SheetValue.PartiallyExpanded
            }
        )

    LaunchedEffect(key1 = LocalLifecycleOwner.current) {
        val status = cameraPermissionState.status
        if (!status.isGranted) {
            cameraPermissionState.launchPermissionRequest()
        }
        onScreenLoaded.invoke(status.isGranted)
    }

    resultBottomSheetStateModel.let {
        LaunchedEffect(key1 = it) {
            when (it) {
                is BarcodeScannerBottomSheetState.Loading -> {
                    resultBottomSheetState.show()
                }
                is BarcodeScannerBottomSheetState.ProductFound -> {
                    resultBottomSheetState.show()
                }
                is BarcodeScannerBottomSheetState.Error -> {
                    resultBottomSheetState.show()
                }
                is BarcodeScannerBottomSheetState.AddProduct -> {
                    resultBottomSheetState.show()
                }
                is BarcodeScannerBottomSheetState.OnlyID -> {
                    resultBottomSheetState.show()
                }
                is BarcodeScannerBottomSheetState.Hidden -> {
                    resultBottomSheetState.hide()
                }
            }
        }
    }

    LaunchedEffect(key1 = resultBottomSheetState) {
        snapshotFlow { resultBottomSheetState.isVisible }.collect { visible ->
            viewModel.onBottomSheetDialogStateChanged(visible)
        }
    }

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val configuration = LocalConfiguration.current

    val cameraPreview = Preview.Builder().build()

    val imageAnalysis =
        ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()

    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

    val previewWidget = remember { PreviewView(context) }

    var isInitializing by remember { mutableStateOf(true) }

    suspend fun setupCameraPreview() {
        isInitializing = true
        val cameraProvider = context.getCameraXProvider()
        // freeze the camera preview
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(
            lifecycleOwner,
            cameraSelector,
            cameraPreview,
            imageAnalysis
        )
        cameraPreview.setSurfaceProvider(previewWidget.surfaceProvider)
        imageAnalysis.setAnalyzer(imageAnalyzer.getAnalyzerExecutor(), imageAnalyzer)
        isInitializing = false
    }

    LaunchedEffect(key1 = configuration) {
        setupCameraPreview()
    }

    LaunchedEffect(freezeCameraPreview) {
        when {
            freezeCameraPreview -> {
                val cameraProvider = context.getCameraXProvider()
                cameraProvider.unbindAll()
            }
            else -> {
                setupCameraPreview()
            }
        }
    }

    Box(contentAlignment = Alignment.TopStart) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
        ) {
            when (cameraPermissionState.status) {
                is PermissionStatus.Granted -> {
                    val screenWidth = remember { mutableIntStateOf(0) }

                    Box(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .onGloballyPositioned { layoutCoordinates ->
                                    screenWidth.intValue = layoutCoordinates.size.width
                                }
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            val width = remember { mutableIntStateOf(0) }
                            val height = remember { mutableIntStateOf(0) }

                            AndroidView(
                                factory = { previewWidget },
                                modifier =
                                    Modifier
                                        .fillMaxSize()
                                        .onGloballyPositioned { layoutCoordinates ->
                                            width.intValue = layoutCoordinates.size.width
                                            height.intValue = layoutCoordinates.size.height
                                            viewModel::onCameraBoundaryReady.invoke(
                                                RectF(
                                                    0F,
                                                    0F,
                                                    width.intValue.toFloat(),
                                                    height.intValue.toFloat()
                                                )
                                            )
                                        }
                            )

                            BarcodeScanningDecorationLayout(
                                width = width.intValue,
                                height = height.intValue,
                                onScanningAreaReady = {
                                    viewModel.onBarcodeScanningAreaReady(
                                        it
                                    )
                                },
                                scanningResult = scanningResult
                            )

                            if (isInitializing) {
                                Box(
                                    modifier =
                                        Modifier
                                            .fillMaxWidth()
                                            .fillMaxHeight(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Loading...",
                                        style = InvenceTheme.typography.bodyLarge
                                    )
//                                    CircularProgressIndicator(
//                                        color = InvenceTheme.colors.primary
//                                    )
                                }
                            }
                        }
                        if (resultBottomSheetStateModel != BarcodeScannerBottomSheetState.Hidden) {
                            BarcodeScannerBottomSheetLayout(
                                resultBottomSheetState = resultBottomSheetState,
                                resultBottomSheetStateModel = resultBottomSheetStateModel,
                                viewModel = viewModel
                            )
                        }
                    }
                }
                is PermissionStatus.Denied -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Text(
                            modifier = Modifier.align(Alignment.Center),
                            text = stringResource(id = R.string.camera_permission_is_denied),
                            style = InvenceTheme.typography.titleMedium
                        )
                    }
                }
            }
        }
        IconButton(
            modifier =
                Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clip(CircleShape)
                    .background(InvenceTheme.colors.neutral10),
            onClick = { viewModel.onToolbarCloseIconClicked() }
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = "nav back stack")
        }
    }
}

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