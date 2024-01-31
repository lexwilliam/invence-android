package com.lexwilliam.inventory.route

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import com.lexwilliam.inventory.component.InventoryColumnCard
import com.lexwilliam.inventory.model.UiProduct

fun LazyListScope.inventoryProductList(
    uiState: InventoryUiState,
    uiProducts: List<UiProduct>
) {
    items(items = uiProducts) { product ->
        InventoryColumnCard(
            product = product.product
        )
    }
}