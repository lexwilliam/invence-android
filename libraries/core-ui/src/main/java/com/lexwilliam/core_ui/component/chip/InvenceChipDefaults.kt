package com.lexwilliam.core_ui.component.chip

import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ChipColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.lexwilliam.core_ui.theme.InvenceTheme

object InvenceChipDefaults {
    @Composable
    fun assistChipColors(
        containerColor: Color = InvenceTheme.colors.secondary,
        labelColor: Color = InvenceTheme.colors.neutral100,
        leadingIconContentColor: Color = InvenceTheme.colors.neutral100,
        trailingIconContentColor: Color = leadingIconContentColor,
        disabledContainerColor: Color = InvenceTheme.colors.neutral30,
        disabledLabelColor: Color = labelColor,
        disabledLeadingIconContentColor: Color = leadingIconContentColor,
        disabledTrailingIconContentColor: Color = disabledLeadingIconContentColor
    ): ChipColors =
        AssistChipDefaults.assistChipColors(
            containerColor = containerColor,
            labelColor = labelColor,
            leadingIconContentColor = leadingIconContentColor,
            trailingIconContentColor = trailingIconContentColor,
            disabledContainerColor = disabledContainerColor,
            disabledLabelColor = disabledLabelColor,
            disabledLeadingIconContentColor = disabledLeadingIconContentColor,
            disabledTrailingIconContentColor = disabledTrailingIconContentColor
        )
}