package com.lexwilliam.inventory.route

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lexwilliam.inventory.component.InventoryColumnCard
import com.lexwilliam.inventory.model.UiProduct

fun LazyListScope.inventoryProductList(
    uiProducts: List<UiProduct>,
    onEvent: (InventoryUiEvent) -> Unit
) {
    items(items = uiProducts) { product ->
        InventoryColumnCard(
            modifier = Modifier.padding(horizontal = 16.dp),
            product = product.product,
            onClick = { onEvent(InventoryUiEvent.ProductClicked(product.product)) }
        )
    }
}