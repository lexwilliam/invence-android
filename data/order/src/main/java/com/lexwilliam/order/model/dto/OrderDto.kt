package com.lexwilliam.order.model.dto

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName
import com.lexwilliam.core.util.validateUUID
import com.lexwilliam.firebase.extensions.toKtxInstant
import com.lexwilliam.firebase.extensions.toTimestamp
import com.lexwilliam.order.model.Order
import kotlinx.datetime.Instant

data class OrderDto(
    val uuid: String? = null,
    val item: OrderItemDto? = null,
    val discounts: Map<String, OrderDiscountDto>? = null,
    val quantity: Int? = null,
    @JvmField @PropertyName("refunded_quantity")
    val refundedQuantity: Int? = null,
    val note: String? = null,
    @JvmField @PropertyName("created_at")
    val createdAt: Timestamp? = null,
    @JvmField @PropertyName("updated_at")
    val updatedAt: Timestamp? = null,
    @JvmField @PropertyName("deleted_at")
    val deletedAt: Timestamp? = null
) {
    fun toDomain() =
        Order(
            uuid = uuid.validateUUID(),
            item = item?.toDomain(),
            discounts = discounts?.map { it.value.toDomain() } ?: emptyList(),
            quantity = quantity ?: 0,
            refundedQuantity = refundedQuantity ?: 0,
            note = note ?: "",
            createdAt = createdAt?.toKtxInstant() ?: Instant.DISTANT_PAST,
            updatedAt = updatedAt?.toKtxInstant(),
            deletedAt = deletedAt?.toKtxInstant()
        )

    companion object {
        fun fromDomain(domain: Order) =
            OrderDto(
                uuid = domain.uuid.toString(),
                item = domain.item?.let { OrderItemDto.fromDomain(it) },
                discounts =
                    domain.discounts
                        .associate { it.uuid.toString() to OrderDiscountDto.fromDomain(it) },
                quantity = domain.quantity,
                refundedQuantity = domain.refundedQuantity,
                note = domain.note,
                createdAt = domain.createdAt.toTimestamp(),
                updatedAt = domain.updatedAt?.toTimestamp(),
                deletedAt = domain.deletedAt?.toTimestamp()
            )
    }
}