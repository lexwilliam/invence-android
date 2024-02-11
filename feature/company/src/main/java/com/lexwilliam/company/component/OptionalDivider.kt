package com.lexwilliam.company.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.lexwilliam.core_ui.theme.InvenceTheme

@Composable
fun OptionalDivider(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Divider(modifier = Modifier.fillMaxWidth())
        Text(text = "Optional", style = InvenceTheme.typography.bodyMedium)
    }
}