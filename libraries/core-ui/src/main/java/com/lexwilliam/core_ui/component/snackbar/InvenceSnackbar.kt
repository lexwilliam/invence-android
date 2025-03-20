package com.lexwilliam.core_ui.component.snackbar

import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import com.lexwilliam.core_ui.theme.InvenceTheme

@Composable
fun InvenceSnackbar(
    snackbarData: SnackbarData,
    modifier: Modifier = Modifier,
    actionOnNewLine: Boolean = false,
    shape: Shape = SnackbarDefaults.shape,
    containerColor: Color = InvenceTheme.colors.primary,
    contentColor: Color = InvenceTheme.colors.secondary,
    actionColor: Color = InvenceTheme.colors.secondary,
    actionContentColor: Color = InvenceTheme.colors.primary,
    dismissActionContentColor: Color = InvenceTheme.colors.secondary
) {
    Snackbar(
        snackbarData,
        modifier,
        actionOnNewLine,
        shape,
        containerColor,
        contentColor,
        actionColor,
        actionContentColor,
        dismissActionContentColor
    )
}