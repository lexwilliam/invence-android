package com.lexwilliam.log.model

import com.lexwilliam.product.model.Product
import java.util.UUID

data class LogUpdate(
    val uuid: UUID,
    val old: Product,
    val new: Product
)