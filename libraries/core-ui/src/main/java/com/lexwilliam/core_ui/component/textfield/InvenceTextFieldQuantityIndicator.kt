package com.lexwilliam.core_ui.component.textfield

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.lexwilliam.core_ui.theme.InvenceTheme

@Composable
fun InvenceTextFieldQuantityIndicator(
    modifier: Modifier = Modifier,
    containerColor: Color,
    contentColor: Color,
    title: String,
    onClick: () -> Unit
) {
    Box(
        modifier =
            modifier
                .size(30.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(containerColor)
                .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(text = title, style = InvenceTheme.typography.bodyMedium, color = contentColor)
    }
}