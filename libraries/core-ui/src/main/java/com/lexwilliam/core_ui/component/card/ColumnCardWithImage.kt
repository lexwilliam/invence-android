package com.lexwilliam.core_ui.component.card

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.lexwilliam.core_ui.component.image.NetworkImage
import com.lexwilliam.core_ui.theme.InvenceTheme

@Composable
fun ColumnCardWithImage(
    modifier: Modifier = Modifier,
    imageModifier: Modifier = Modifier,
    imagePath: Uri? = null,
    content: @Composable () -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(modifier = Modifier.shadow(4.dp, RoundedCornerShape(8.dp), true)) {
            NetworkImage(
                imagePath = imagePath,
                modifier =
                    imageModifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(InvenceTheme.colors.neutral10)
            )
        }
        content()
    }
}