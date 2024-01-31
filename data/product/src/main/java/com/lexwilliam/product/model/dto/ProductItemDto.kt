package com.lexwilliam.product.model.dto

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName
import com.lexwilliam.firebase.toKtxInstant
import com.lexwilliam.firebase.toTimestamp
import com.lexwilliam.product.model.ProductItem
import kotlinx.datetime.Instant

data class ProductItemDto(
    val quantity: Int? = null,
    @JvmField @PropertyName("buy_price")
    val buyPrice: Double? = null,
    @JvmField @PropertyName("created_at")
    val createdAt: Timestamp? = null,
    @JvmField @PropertyName("deleted_at")
    val deletedAt: Timestamp? = null
) {
    fun toDomain() =
        ProductItem(
            quantity = quantity ?: 0,
            buyPrice = buyPrice ?: 0.0,
            createdAt = createdAt?.toKtxInstant() ?: Instant.DISTANT_PAST,
            deletedAt = deletedAt?.toKtxInstant()
        )

    companion object {
        fun fromDomain(domain: ProductItem) =
            ProductItemDto(
                quantity = domain.quantity,
                buyPrice = domain.buyPrice,
                createdAt = domain.createdAt.toTimestamp(),
                deletedAt = domain.deletedAt?.toTimestamp()
            )
    }
}