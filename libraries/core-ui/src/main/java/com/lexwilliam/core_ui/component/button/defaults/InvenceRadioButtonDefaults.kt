package com.lexwilliam.core_ui.component.button.defaults

import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.lexwilliam.core_ui.theme.InvenceTheme

object InvenceRadioButtonDefaults {
    @Composable
    fun colors(
        selectedColor: Color = InvenceTheme.colors.primary,
        unselectedColor: Color = InvenceTheme.colors.primary,
        disabledSelectedColor: Color = InvenceTheme.colors.neutral60,
        disabledUnselectedColor: Color = InvenceTheme.colors.neutral60
    ) = RadioButtonDefaults.colors(
        selectedColor = selectedColor,
        unselectedColor = unselectedColor,
        disabledSelectedColor = disabledSelectedColor,
        disabledUnselectedColor = disabledUnselectedColor
    )
}