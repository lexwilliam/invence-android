package com.lexwilliam.log.model

import java.util.UUID

data class LogRestock(
    val uuid: UUID,
    val productUUID: String,
    val name: String,
    val originalStock: Int,
    val addedStock: Int,
    val price: Double
) {
    val updatedStock = originalStock + addedStock
    val total = price * addedStock
}