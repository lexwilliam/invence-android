package com.lexwilliam.log.model

import com.lexwilliam.order.model.OrderGroup
import com.lexwilliam.product.model.Product
import java.util.UUID

data class LogSell(
    val uuid: UUID,
    val orderGroup: OrderGroup,
    val soldProducts: List<Product>,
    val totalProfit: Double
)