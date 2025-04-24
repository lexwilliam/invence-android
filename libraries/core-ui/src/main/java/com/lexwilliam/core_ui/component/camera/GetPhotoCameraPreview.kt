package com.lexwilliam.core_ui.component.camera

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import android.view.ViewGroup
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.OnImageCapturedCallback
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.core.resolutionselector.ResolutionStrategy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import com.lexwilliam.core_ui.theme.InvenceTheme
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GetPhotoCameraPreview(
    onDismiss: () -> Unit,
    onPhotoTaken: (Bitmap) -> Unit
) {
    val lensFacing = CameraSelector.LENS_FACING_BACK
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val aspectRatio = AspectRatio.RATIO_4_3
    val fallbackRule = AspectRatioStrategy.FALLBACK_RULE_AUTO
    val resolutionSelector =
        ResolutionSelector.Builder()
            .setResolutionStrategy(ResolutionStrategy.HIGHEST_AVAILABLE_STRATEGY)
            .setAspectRatioStrategy(AspectRatioStrategy(aspectRatio, fallbackRule))
            .build()
    val preview =
        Preview.Builder()
            .setResolutionSelector(resolutionSelector)
            .build()
    val previewView =
        remember {
            PreviewView(context).apply {
                this.scaleType = PreviewView.ScaleType.FILL_CENTER
                layoutParams =
                    ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                implementationMode = PreviewView.ImplementationMode.COMPATIBLE
            }
        }
    val cameraxSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
    val imageCapture =
        remember {
            ImageCapture.Builder()
                .setResolutionSelector(resolutionSelector)
                .build()
        }
    LaunchedEffect(lensFacing) {
        val cameraProvider = context.getCameraProvider()
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(lifecycleOwner, cameraxSelector, preview, imageCapture)
        preview.setSurfaceProvider(previewView.surfaceProvider)
    }

    val coroutineScope = rememberCoroutineScope()

    fun dismissCamera() {
        coroutineScope.launch {
            Log.d("TAG", "onDismissRequest")
            val cameraProvider = context.getCameraProvider()
            cameraProvider.unbindAll()
            onDismiss()
        }
    }

    Dialog(
        onDismissRequest = {
            dismissCamera()
        },
        properties =
            DialogProperties(
                usePlatformDefaultWidth = false
            )
    ) {
        Column(
            modifier =
                Modifier
                    .background(InvenceTheme.colors.neutral100)
                    .padding(bottom = 80.dp),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            CenterAlignedTopAppBar(
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = InvenceTheme.colors.neutral100,
                        navigationIconContentColor = InvenceTheme.colors.neutral10,
                        actionIconContentColor = InvenceTheme.colors.neutral10,
                        titleContentColor = InvenceTheme.colors.neutral10
                    ),
                title = {
                    Text(
                        text = "Take Photo",
                        style = InvenceTheme.typography.titleMedium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        dismissCamera()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "dismiss camera icon"
                        )
                    }
                }
            )
            AndroidView(
                factory = { previewView },
                modifier = Modifier.weight(1f)
            )
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .background(InvenceTheme.colors.neutral100)
                        .padding(horizontal = 16.dp, vertical = 32.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier =
                        Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(InvenceTheme.colors.neutral10)
                            .clickable {
                                imageCapture.takePicture(
                                    ContextCompat.getMainExecutor(context),
                                    object : OnImageCapturedCallback() {
                                        override fun onCaptureSuccess(image: ImageProxy) {
                                            super.onCaptureSuccess(image)
                                            val croppedBitmap = cropToSquare(image.toBitmap())
                                            val rotatedBitmap = rotateBitmap(croppedBitmap, 90f)
                                            onPhotoTaken(rotatedBitmap)
                                            dismissCamera()
                                        }

                                        override fun onError(exception: ImageCaptureException) {
                                            super.onError(exception)
                                        }
                                    }
                                )
                            },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = "take a picture")
                }
            }
        }
    }
}

private suspend fun Context.getCameraProvider(): ProcessCameraProvider =
    suspendCoroutine { continuation ->
        ProcessCameraProvider.getInstance(this).also { cameraProvider ->
            cameraProvider.addListener({
                continuation.resume(cameraProvider.get())
            }, ContextCompat.getMainExecutor(this))
        }
    }

fun cropToSquare(bitmap: Bitmap): Bitmap {
    val size = minOf(bitmap.width, bitmap.height)
    val x = (bitmap.width - size) / 2
    val y = (bitmap.height - size) / 2
    return Bitmap.createBitmap(bitmap, x, y, size, size)
}

fun rotateBitmap(
    source: Bitmap,
    angle: Float
): Bitmap {
    val matrix = Matrix()
    matrix.postRotate(angle)
    return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
}