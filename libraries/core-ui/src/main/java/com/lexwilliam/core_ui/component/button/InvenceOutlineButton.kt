package com.lexwilliam.core_ui.component.button

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.lexwilliam.core_ui.component.button.defaults.InvenceButtonDefaults
import com.lexwilliam.core_ui.component.loading.InvenceCircularProgressIndicator
import com.lexwilliam.core_ui.theme.InvenceTheme

@Composable
fun InvenceOutlineButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = ButtonDefaults.shape,
    colors: ButtonColors = InvenceButtonDefaults.outlineButtonColors(),
    elevation: ButtonElevation? = ButtonDefaults.buttonElevation(),
    isLoading: Boolean = false,
    border: BorderStroke? = BorderStroke(width = 1.dp, color = InvenceTheme.colors.primary),
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable RowScope.() -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = if (isLoading) false else enabled,
        shape = shape,
        colors = colors,
        elevation = elevation,
        border = border,
        contentPadding = contentPadding,
        interactionSource = interactionSource
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (isLoading) {
                InvenceCircularProgressIndicator(
                    color = InvenceTheme.colors.primary,
                    modifier = Modifier.size(16.dp)
                )
                Text("Loading...", style = InvenceTheme.typography.bodyMedium)
            } else {
                content()
            }
        }
    }
}