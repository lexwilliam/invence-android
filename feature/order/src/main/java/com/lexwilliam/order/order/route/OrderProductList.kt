package com.lexwilliam.order.order.route

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lexwilliam.order.order.component.OrderProductCard
import com.lexwilliam.order.order.model.UiCartItem
import com.lexwilliam.order.order.model.UiProduct

fun LazyListScope.orderProductList(
    uiProducts: List<UiProduct>,
    cart: List<UiCartItem>,
    onEvent: (OrderUiEvent) -> Unit
) {
    items(items = uiProducts) { product ->
        OrderProductCard(
            modifier = Modifier.padding(horizontal = 16.dp),
            imagePath = product.product.imagePath,
            name = product.product.name,
            price = product.product.sellPrice,
            stock = product.product.quantity,
            quantity =
                cart
                    .firstOrNull { item -> item.product.sku == product.product.sku }
                    ?.quantity ?: 0,
            onQuantityChanged = {
                onEvent(OrderUiEvent.QuantityChanged(product.product, it))
            }
        )
    }
}