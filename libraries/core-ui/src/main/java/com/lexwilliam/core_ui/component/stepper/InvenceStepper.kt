package com.lexwilliam.core_ui.component.stepper

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.lexwilliam.core_ui.theme.InvenceTheme

@Composable
fun InvenceStepper(
    modifier: Modifier = Modifier,
    quantity: Int,
    onQuantityChanged: (Int) -> Unit
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier =
                modifier
                    .clip(CircleShape)
                    .background(
                        color = InvenceTheme.colors.primary
                    )
                    .padding(4.dp)
                    .wrapContentSize()
                    .clickable { onQuantityChanged(if (quantity == 0) 0 else quantity - 1) }
        ) {
            Icon(
                modifier = Modifier.size(14.dp),
                imageVector = Icons.Default.Remove,
                contentDescription = "decrement quantity icon",
                tint = InvenceTheme.colors.neutral10
            )
        }
        Box(modifier = Modifier.defaultMinSize(24.dp), contentAlignment = Alignment.Center) {
            BasicTextField(
                modifier = Modifier.width(IntrinsicSize.Min),
                value = quantity.toString(),
                onValueChange = { onQuantityChanged(it.toIntOrNull() ?: 0) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                textStyle = InvenceTheme.typography.titleMedium,
                singleLine = true
            )
        }

        Box(
            modifier =
                modifier
                    .clip(CircleShape)
                    .background(
                        color = InvenceTheme.colors.primary
                    )
                    .padding(4.dp)
                    .wrapContentSize()
                    .clickable { onQuantityChanged(quantity + 1) }
        ) {
            Icon(
                modifier = Modifier.size(14.dp),
                imageVector = Icons.Default.Add,
                contentDescription = "increment quantity icon",
                tint = InvenceTheme.colors.neutral10
            )
        }
    }
}