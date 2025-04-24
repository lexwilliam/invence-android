package com.lexwilliam.core_ui.component.loading

import android.R.attr.strokeWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.lexwilliam.core_ui.theme.InvenceTheme

@Composable
fun InvenceCircularProgressIndicator(
    modifier: Modifier = Modifier,
    color: Color = InvenceTheme.colors.primary,
    strokeWidth: Dp = 4.dp,
    trackColor: Color = Color.Transparent,
    strokeCap: StrokeCap = StrokeCap.Square
) {
    CircularProgressIndicator(
        modifier = modifier,
        color = color,
        strokeWidth = strokeWidth,
        trackColor = trackColor,
        strokeCap = strokeCap
    )
}