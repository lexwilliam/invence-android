package com.lexwilliam.core_ui.component.chip

import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ChipColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.SelectableChipColors
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

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun filterChipColors(
        containerColor: Color = Color.Transparent,
        labelColor: Color = InvenceTheme.colors.primary,
        iconColor: Color = InvenceTheme.colors.primary,
        disabledContainerColor: Color = Color.Transparent,
        disabledLabelColor: Color = InvenceTheme.colors.neutral40,
        disabledLeadingIconColor: Color = InvenceTheme.colors.neutral40,
        disabledTrailingIconColor: Color = disabledLeadingIconColor,
        selectedContainerColor: Color = InvenceTheme.colors.secondary,
        disabledSelectedContainerColor: Color =
            InvenceTheme.colors.neutral30,
        selectedLabelColor: Color = InvenceTheme.colors.primary,
        selectedLeadingIconColor: Color = InvenceTheme.colors.primary,
        selectedTrailingIconColor: Color = selectedLeadingIconColor
    ): SelectableChipColors =
        FilterChipDefaults.filterChipColors(
            containerColor = containerColor,
            labelColor = labelColor,
            iconColor = iconColor,
            disabledContainerColor = disabledContainerColor,
            disabledLabelColor = disabledLabelColor,
            disabledLeadingIconColor = disabledLeadingIconColor,
            disabledTrailingIconColor = disabledTrailingIconColor,
            selectedContainerColor = selectedContainerColor,
            disabledSelectedContainerColor = disabledSelectedContainerColor,
            selectedLabelColor = selectedLabelColor,
            selectedLeadingIconColor = selectedLeadingIconColor,
            selectedTrailingIconColor = selectedTrailingIconColor
        )
}