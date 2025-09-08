package com.lexwilliam.core_ui.component.brand

import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.lexwilliam.core_ui.R

@Composable
fun InvenceLogo(modifier: Modifier = Modifier) {
    Image(
        modifier =
            modifier
                .clip(RoundedCornerShape(16.dp)),
        painter = painterResource(R.drawable.ic_launcher_foreground),
        contentDescription = "Invence Logo"
    )
}