package com.lexwilliam.core_ui.component.chip

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ChipColors
import androidx.compose.material3.ChipElevation
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun InvenceChip(
    onClick: () -> Unit,
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    shape: Shape = CircleShape,
    colors: ChipColors = InvenceChipDefaults.assistChipColors(),
    elevation: ChipElevation? = AssistChipDefaults.assistChipElevation(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    AssistChip(
        onClick = onClick,
        label = label,
        modifier = modifier,
        enabled = enabled,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        shape = shape,
        colors = colors,
        elevation = elevation,
        border = null,
        interactionSource = interactionSource
    )
}

@Preview(showBackground = true)
@Composable
fun InvenceChipPreview() {
    InvenceChip(onClick = { /*TODO*/ }, label = { Text("Chip") })
}