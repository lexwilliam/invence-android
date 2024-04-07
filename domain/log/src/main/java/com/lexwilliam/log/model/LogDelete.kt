package com.lexwilliam.log.model

import com.lexwilliam.product.model.Product
import java.util.UUID

data class LogDelete(
    val uuid: UUID,
    val product: Product
)