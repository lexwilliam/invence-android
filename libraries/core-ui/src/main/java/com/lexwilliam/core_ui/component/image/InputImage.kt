package com.lexwilliam.core_ui.component.image

import android.annotation.SuppressLint
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.lexwilliam.core.model.UploadImageFormat
import com.lexwilliam.core_ui.R
import com.lexwilliam.core_ui.component.button.InvencePrimaryButton
import com.lexwilliam.core_ui.extension.dashedBorder
import com.lexwilliam.core_ui.theme.InvenceTheme

@SuppressLint("UnrememberedMutableInteractionSource")
@Composable
fun InputImage(
    modifier: Modifier = Modifier,
    imageModifier: Modifier = Modifier,
    imagePath: UploadImageFormat? = null,
    label: String? = null,
    onImageChanged: (Uri?) -> Unit,
    enableCamera: Boolean = false,
    onCameraClicked: () -> Unit = {}
) {
    val photoPickerLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia(),
            onResult = onImageChanged
        )

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier =
                Modifier.clickable(
                    interactionSource = MutableInteractionSource(),
                    indication = null,
                    onClick = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(
                                ActivityResultContracts
                                    .PickVisualMedia.ImageOnly
                            )
                        )
                    }
                )
        ) {
            if (imagePath != null) {
                when (imagePath) {
                    is UploadImageFormat.WithUri -> {
                        NetworkImage(
                            modifier =
                                imageModifier
                                    .padding(top = 8.dp)
                                    .shadow(
                                        elevation = 2.dp,
                                        shape = RoundedCornerShape(16.dp),
                                        clip = true
                                    ),
                            imagePath = imagePath.uri
                        )
                    }
                    is UploadImageFormat.WithBitmap -> {
                        NetworkImage(
                            modifier =
                                imageModifier
                                    .padding(top = 8.dp)
                                    .shadow(
                                        elevation = 2.dp,
                                        shape = RoundedCornerShape(16.dp),
                                        clip = true
                                    ),
                            imagePath = imagePath.bitmap
                        )
                    }
                }
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
                                Modifier
                                    .size(72.dp),
                            painter = painterResource(id = R.drawable.cloud_upload),
                            contentDescription = "upload image icon",
                            tint = InvenceTheme.colors.neutral80
                        )
                        if (label != null) {
                            Text(
                                text = label,
                                style = InvenceTheme.typography.bodyLarge,
                                color = InvenceTheme.colors.neutral80
                            )
                        }
                    }
                }
            }
        }
        if (enableCamera) {
            InvencePrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = onCameraClicked
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = "camera icon")
                    Text(text = "Use camera", style = InvenceTheme.typography.labelLarge)
                }
            }
        }
    }
}