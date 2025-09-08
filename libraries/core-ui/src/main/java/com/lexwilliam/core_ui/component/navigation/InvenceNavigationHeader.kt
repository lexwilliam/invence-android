package com.lexwilliam.core_ui.component.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.lexwilliam.core_ui.component.brand.InvenceLogo
import com.lexwilliam.core_ui.component.brand.InvenceLogoText

@Composable
fun InvenceNavigationHeader(
    modifier: Modifier = Modifier,
    isCompact: Boolean = false
) {
    if (isCompact) {
        InvenceLogo(
            modifier = modifier
        )
    } else {
        InvenceLogoText(
            modifier = modifier
        )
    }
}