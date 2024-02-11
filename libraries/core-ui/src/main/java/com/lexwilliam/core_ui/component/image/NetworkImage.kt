package com.lexwilliam.core_ui.component.image

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.lexwilliam.core_ui.theme.InvenceTheme

@Composable
fun NetworkImage(
    modifier: Modifier = Modifier,
    imagePath: Uri? = null
) {
    if (imagePath == null) {
        Box(
            modifier =
                modifier.background(
                    color = InvenceTheme.colors.neutral40
                )
        )
    } else {
        AsyncImage(
            model =
                ImageRequest.Builder(LocalContext.current)
                    .data(imagePath)
                    .crossfade(true)
                    .build(),
            contentDescription = "network coil image",
            contentScale = ContentScale.Crop,
            modifier =
            modifier
        )
    }
}

@Composable
fun NetworkImage(
    modifier: Modifier = Modifier,
    imagePath: Bitmap? = null
) {
    if (imagePath == null) {
        Box(
            modifier =
                modifier.background(
                    color = InvenceTheme.colors.neutral40
                )
        )
    } else {
        AsyncImage(
            model =
                ImageRequest.Builder(LocalContext.current)
                    .data(imagePath)
                    .crossfade(true)
                    .build(),
            contentDescription = "network coil image",
            contentScale = ContentScale.Crop,
            modifier =
            modifier
        )
    }
}