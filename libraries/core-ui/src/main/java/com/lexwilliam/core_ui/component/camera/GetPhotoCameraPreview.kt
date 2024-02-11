package com.lexwilliam.core_ui.component.camera

import android.graphics.Bitmap
import android.util.Log
import androidx.camera.core.ImageCapture.OnImageCapturedCallback
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.lexwilliam.core_ui.theme.InvenceTheme

@Composable
fun GetPhotoCameraPreview(onPhotoTaken: (Bitmap) -> Unit) {
    val context = LocalContext.current
    val controller =
        remember {
            LifecycleCameraController(context).apply {
                setEnabledUseCases(
                    CameraController.IMAGE_CAPTURE
                )
            }
        }

    val lifecycleOwner = LocalLifecycleOwner.current
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        AndroidView(
            factory = {
                PreviewView(it).apply {
                    this.controller = controller
                    controller.bindToLifecycle(lifecycleOwner)
                }
            },
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier =
                Modifier
                    .padding(16.dp)
                    .align(Alignment.BottomCenter)
        ) {
            IconButton(
                modifier =
                    Modifier
                        .clip(CircleShape)
                        .background(InvenceTheme.colors.neutral10)
                        .padding(8.dp),
                onClick = {
                    controller.takePicture(
                        ContextCompat.getMainExecutor(context),
                        object : OnImageCapturedCallback() {
                            override fun onCaptureSuccess(image: ImageProxy) {
                                super.onCaptureSuccess(image)
                                onPhotoTaken(image.toBitmap())
                            }

                            override fun onError(exception: ImageCaptureException) {
                                super.onError(exception)
                                Log.e("Camera", "Error")
                            }
                        }
                    )
                }
            ) {
                Icon(Icons.Default.CameraAlt, contentDescription = "take a picture")
            }
        }
    }
}