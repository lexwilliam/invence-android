package com.lexwilliam.inventory.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lexwilliam.core.extensions.toCurrency
import com.lexwilliam.core_ui.component.card.ColumnCardWithImage
import com.lexwilliam.core_ui.theme.InvenceTheme
import com.lexwilliam.product.model.Product

@Composable
fun InventoryColumnCard(
    modifier: Modifier = Modifier,
    imageModifier: Modifier = Modifier,
    contentModifier: Modifier = Modifier,
    onClick: () -> Unit,
    product: Product
) {
    ColumnCardWithImage(
        modifier =
            modifier.clickable(
                interactionSource = MutableInteractionSource(),
                indication = null,
                onClick = onClick
            ),
        imageModifier = imageModifier,
        imagePath = product.imagePath
    ) {
        Column(
            modifier = contentModifier.padding(vertical = 16.dp)
        ) {
            Text(text = product.name, style = InvenceTheme.typography.bodyLarge)
            Text(text = product.sellPrice.toCurrency(), style = InvenceTheme.typography.labelMedium)
        }
    }
}