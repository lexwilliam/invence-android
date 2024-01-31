package com.lexwilliam.inventory.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lexwilliam.core_ui.component.card.ColumnCardWithImage
import com.lexwilliam.core_ui.theme.InvenceTheme
import com.lexwilliam.product.model.Product

@Composable
fun InventoryColumnCard(
    modifier: Modifier = Modifier,
    imageModifier: Modifier = Modifier,
    contentModifier: Modifier = Modifier,
    product: Product
) {
    ColumnCardWithImage(
        modifier = modifier,
        imageModifier = imageModifier,
        imagePath = product.imagePath
    ) {
        Column(
            modifier = contentModifier.padding(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = product.name, style = InvenceTheme.typography.bodyLarge)
            Text(text = product.sellPrice.toString(), style = InvenceTheme.typography.labelMedium)
        }
    }
}