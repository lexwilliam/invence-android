package com.lexwilliam.core_ui.component.image

import android.annotation.SuppressLint
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.lexwilliam.core_ui.R
import com.lexwilliam.core_ui.component.button.InvenceTextButton
import com.lexwilliam.core_ui.component.camera.GetPhotoCameraPreview
import com.lexwilliam.core_ui.extension.dashedBorder
import com.lexwilliam.core_ui.theme.InvenceTheme

@SuppressLint("UnrememberedMutableInteractionSource")
@Composable
fun InputImage(
    modifier: Modifier = Modifier,
    imageModifier: Modifier = Modifier,
    image: Any? = null,
    label: String? = null,
    onImageChanged: (Any?) -> Unit
) {
    val context = LocalContext.current
    var isDialogShowing by remember { mutableStateOf(false) }
    var cameraOpen by remember { mutableStateOf(false) }

    val photoPickerLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia(),
            onResult = { uri ->
                onImageChanged(uri)
                cameraOpen = false
                isDialogShowing = false
            }
        )

    if (isDialogShowing) {
        Dialog(
            onDismissRequest = { isDialogShowing = false }
        ) {
            Column(
                modifier =
                    Modifier
                        .shadow(8.dp, shape = RoundedCornerShape(8.dp))
                        .background(InvenceTheme.colors.neutral10)
                        .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Choose method", style = InvenceTheme.typography.titleMedium)
                InvenceTextButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { cameraOpen = true }
                ) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Take Photo", style = InvenceTheme.typography.bodyMedium)
                        Icon(Icons.Default.ChevronRight, contentDescription = "take photo icon")
                    }
                }
                InvenceTextButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(
                                ActivityResultContracts
                                    .PickVisualMedia.ImageOnly
                            )
                        )
                    }
                ) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Choose Photo", style = InvenceTheme.typography.bodyMedium)
                        Icon(Icons.Default.ChevronRight, contentDescription = "choose photo icon")
                    }
                }
            }
        }
    }

    if (cameraOpen) {
        GetPhotoCameraPreview(
            onDismiss = { cameraOpen = false },
            onPhotoTaken = { bmp ->
                onImageChanged(bmp)
                cameraOpen = false
                isDialogShowing = false
            }
        )
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier =
                Modifier.clickable(
                    interactionSource = MutableInteractionSource(),
                    indication = null,
                    onClick = { isDialogShowing = true }
                )
        ) {
            if (image != null) {
                NetworkImage(
                    modifier =
                        imageModifier
                            .padding(top = 8.dp)
                            .shadow(
                                elevation = 2.dp,
                                shape = RoundedCornerShape(16.dp),
                                clip = true
                            ),
                    imagePath = image
                )
            } else {
                Box(
                    modifier =
                        imageModifier
                            .padding(top = 8.dp)
                            .dashedBorder(
                                color = InvenceTheme.colors.neutral50,
                                shape = RoundedCornerShape(16.dp)
                            ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier =
                            Modifier
                                .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            modifier =
                                Modifier.fillMaxSize(0.7f),
                            painter = painterResource(id = R.drawable.cloud_upload),
                            contentDescription = "upload image icon",
                            tint = InvenceTheme.colors.neutral80
                        )
                    }
                }
            }
        }
    }
}