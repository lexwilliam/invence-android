package com.lexwilliam.core_ui.component.brand

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.lexwilliam.core_ui.theme.InvenceTheme

@Composable
fun InvenceLogoText() {
    Text(text = "Invence", style = InvenceTheme.typography.brand, color = InvenceTheme.colors.primary)
}
