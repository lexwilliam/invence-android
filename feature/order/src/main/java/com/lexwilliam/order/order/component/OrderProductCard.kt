package com.lexwilliam.order.order.component

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.lexwilliam.core.extensions.toCurrency
import com.lexwilliam.core_ui.component.image.NetworkImage
import com.lexwilliam.core_ui.component.stepper.InvenceStepper
import com.lexwilliam.core_ui.theme.InvenceTheme

@Composable
fun OrderProductCard(
    modifier: Modifier = Modifier,
    imageModifier: Modifier = Modifier,
    imagePath: Uri?,
    name: String,
    price: Double,
    quantity: Int,
    onQuantityChanged: (Int) -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier =
                Modifier
                    .shadow(4.dp, RoundedCornerShape(8.dp), true)
        ) {
            NetworkImage(
                imagePath = imagePath,
                modifier =
                    imageModifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(InvenceTheme.colors.neutral10)
            )
        }
        Column(
            modifier =
                Modifier
                    .defaultMinSize(minHeight = 120.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = name, style = InvenceTheme.typography.labelLarge)
                Text(
                    text = price.toCurrency(),
                    style = InvenceTheme.typography.bodyMedium
                )
            }
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                InvenceStepper(quantity = quantity, onQuantityChanged = onQuantityChanged)
            }
        }
    }
}

@Composable
fun SmallOrderProductCard(
    modifier: Modifier = Modifier,
    imageModifier: Modifier = Modifier,
    imagePath: Uri?,
    name: String,
    price: Double,
    quantity: Int,
    onQuantityChanged: (Int) -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(6f),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier =
                    Modifier
                        .shadow(4.dp, RoundedCornerShape(8.dp), true)
            ) {
                NetworkImage(
                    imagePath = imagePath,
                    modifier =
                        imageModifier
                            .size(50.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(InvenceTheme.colors.neutral10)
                )
            }
            Column(
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = name, style = InvenceTheme.typography.labelLarge)
                    Text(
                        text = price.toCurrency(),
                        style = InvenceTheme.typography.bodyMedium
                    )
                }
            }
        }
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(4f),
            contentAlignment = Alignment.CenterEnd
        ) {
            InvenceStepper(quantity = quantity, onQuantityChanged = onQuantityChanged)
        }
    }
}