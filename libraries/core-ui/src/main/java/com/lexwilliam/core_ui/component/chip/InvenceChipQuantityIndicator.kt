package com.lexwilliam.core_ui.component.chip

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.lexwilliam.core_ui.theme.InvenceTheme

@Composable
fun InvenceChipQuantityIndicator(
    modifier: Modifier = Modifier,
    quantity: Int
) {
    Box(
        modifier =
            modifier
                .background(
                    color = InvenceTheme.colors.primary,
                    shape = CircleShape
                )
                .wrapContentSize()
    ) {
        Text(text = quantity.toString(), style = InvenceTheme.typography.titleMedium)
    }
}