package com.lexwilliam.core_ui.component.button.defaults

import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.lexwilliam.core_ui.theme.InvenceTheme

object InvenceButtonDefaults {
    @Composable
    fun baseButtonColors(
        containerColor: Color,
        contentColor: Color,
        disabledContainerColor: Color = InvenceTheme.colors.neutral40,
        disabledContentColor: Color = InvenceTheme.colors.neutral60
    ) = ButtonDefaults.buttonColors(
        containerColor = containerColor,
        contentColor = contentColor,
        disabledContainerColor = disabledContainerColor,
        disabledContentColor = disabledContentColor
    )

    @Composable
    fun primaryButtonColors(
        containerColor: Color = InvenceTheme.colors.primary,
        contentColor: Color = InvenceTheme.colors.neutral10
    ) = baseButtonColors(
        containerColor = containerColor,
        contentColor = contentColor
    )

    @Composable
    fun secondaryButtonColors(
        containerColor: Color = InvenceTheme.colors.neutral10,
        contentColor: Color = InvenceTheme.colors.primary
    ) = baseButtonColors(
        containerColor = containerColor,
        contentColor = contentColor
    )
}