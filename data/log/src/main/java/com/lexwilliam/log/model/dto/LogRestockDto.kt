package com.lexwilliam.log.model.dto

import com.google.firebase.firestore.PropertyName
import com.lexwilliam.core.util.validateUUID
import com.lexwilliam.log.model.LogRestock

data class LogRestockDto(
    val uuid: String? = null,
    val name: String? = null,
    @JvmField @PropertyName("product_uuid")
    val productUUID: String? = null,
    @JvmField @PropertyName("original_stock")
    val originalStock: Int? = null,
    @JvmField @PropertyName("added_stock")
    val addedStock: Int? = null,
    val price: Double? = null
) {
    fun toDomain(): LogRestock =
        LogRestock(
            uuid = uuid.validateUUID(),
            name = name ?: "",
            productUUID = productUUID ?: "",
            originalStock = originalStock ?: 0,
            addedStock = addedStock ?: 0,
            price = price ?: 0.0
        )

    companion object {
        fun fromDomain(domain: LogRestock): LogRestockDto =
            LogRestockDto(
                uuid = domain.uuid.toString(),
                name = domain.name,
                productUUID = domain.productUUID,
                originalStock = domain.originalStock,
                addedStock = domain.addedStock,
                price = domain.price
            )
    }
}