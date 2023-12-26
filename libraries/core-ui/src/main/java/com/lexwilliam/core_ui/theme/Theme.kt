package com.lexwilliam.core_ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

@Composable
fun InvenceTheme(content: @Composable () -> Unit) {
    val colors = InvenceLightColors
    val typography = InvenceMobileTypography
    CompositionLocalProvider(
        LocalInvenceColors provides colors,
        LocalInvenceTypography provides typography,
    ) {
        content()
    }
}

object InvenceTheme {
    val colors: InvenceColors
        @Composable
        get() = LocalInvenceColors.current
    val typography: InvenceTypography
        @Composable
        get() = LocalInvenceTypography.current
}
