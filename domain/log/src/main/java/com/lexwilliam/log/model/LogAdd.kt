package com.lexwilliam.log.model

import com.lexwilliam.product.model.Product
import java.util.UUID

data class LogAdd(
    val uuid: UUID,
    val product: Product
) {
    val totalExpense = product.items.sumOf { it.buyPrice * it.quantity }
}