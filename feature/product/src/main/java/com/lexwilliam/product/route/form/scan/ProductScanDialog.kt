package com.lexwilliam.product.route.form.scan

import android.Manifest
import android.graphics.RectF
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.barcode.component.BarcodeScanningDecorationLayout
import com.example.barcode.extension.getCameraXProvider
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.lexwilliam.barcode.R
import com.lexwilliam.core_ui.component.topbar.InvenceTopBar
import com.lexwilliam.core_ui.theme.InvenceTheme
import com.lexwilliam.inventory.scan.ProductScanEvent
import com.lexwilliam.product.route.form.ProductFormViewModel

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@androidx.annotation.OptIn(ExperimentalGetImage::class)
@Composable
fun ProductScanDialog(viewModel: ProductFormViewModel) {
    val scanningResult by viewModel.scanningResult.collectAsStateWithLifecycle()
    val freezeCameraPreview by viewModel.freezeCameraPreview.collectAsStateWithLifecycle(false)
    val resultBottomSheetStateModel by
        viewModel.resultBottomSheetState.collectAsStateWithLifecycle()

    val imageAnalyzer = viewModel.getBarcodeImageAnalyzer()

    val cameraPermissionState =
        rememberPermissionState(
            Manifest.permission.CAMERA
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
    }

    resultBottomSheetStateModel.let {
        LaunchedEffect(key1 = it) {
            when (it) {
                is ProductScanBottomSheetState.Loading -> {
                    resultBottomSheetState.show()
                }
                is ProductScanBottomSheetState.ScanResult -> {
                    resultBottomSheetState.show()
                }
                is ProductScanBottomSheetState.Error -> {
                    resultBottomSheetState.show()
                }
                is ProductScanBottomSheetState.Hidden -> {
                    resultBottomSheetState.hide()
                }
            }
        }
    }

    LaunchedEffect(key1 = resultBottomSheetState) {
        snapshotFlow { resultBottomSheetState.isVisible }.collect { visible ->
            viewModel.onScanEvent(ProductScanEvent.BottomSheetDialogStateChanged(visible))
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

    Dialog(
        onDismissRequest = { viewModel.onScanEvent(ProductScanEvent.Dismiss) },
        properties =
            DialogProperties(
                usePlatformDefaultWidth = false,
                dismissOnBackPress = true
            )
    ) {
        Scaffold(
            topBar = {
                InvenceTopBar(
                    title = {
                        Text("Scan Product", style = InvenceTheme.typography.titleMedium)
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { viewModel.onScanEvent(ProductScanEvent.Dismiss) }
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "back"
                            )
                        }
                    }
                )
            }
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
                                            viewModel.onScanEvent(
                                                ProductScanEvent.CameraBoundaryReady(
                                                    RectF(
                                                        0F,
                                                        0F,
                                                        width.intValue.toFloat(),
                                                        height.intValue.toFloat()
                                                    )
                                                )
                                            )
                                        }
                            )

                            BarcodeScanningDecorationLayout(
                                width = width.intValue,
                                height = height.intValue,
                                onScanningAreaReady = {
                                    viewModel.onScanEvent(ProductScanEvent.ScanningAreaReady(it))
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
                                }
                            }
                        }
                        if (resultBottomSheetStateModel != ProductScanBottomSheetState.Hidden) {
                            ProductScanBottomSheet(
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
    }
}