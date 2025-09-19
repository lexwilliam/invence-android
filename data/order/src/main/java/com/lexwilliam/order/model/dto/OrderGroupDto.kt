package com.lexwilliam.order.model.dto

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName
import com.lexwilliam.core.util.validateUUID
import com.lexwilliam.firebase.extensions.toKtxInstant
import com.lexwilliam.firebase.extensions.toTimestamp
import com.lexwilliam.order.model.OrderGroup
import kotlinx.datetime.Instant
import kotlinx.serialization.Contextual
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@OptIn(InternalSerializationApi::class)
@Serializable
data class OrderGroupDto(
    val uuid: String? = null,
    @SerialName("user_uuid")
    @JvmField
    @PropertyName("user_uuid")
    val userUUID: String? = null,
    @SerialName("created_by")
    @JvmField
    @PropertyName("created_by")
    val createdBy: String? = null,
    val orders: Map<String, OrderDto>? = null,
    val taxes: Map<String, OrderTaxDto>? = null,
    val discounts: Map<String, OrderDiscountDto>? = null,
    @SerialName("created_at")
    @JvmField
    @PropertyName("created_at")
    @Contextual
    val createdAt: Timestamp? = null,
    @SerialName("deleted_at")
    @JvmField
    @PropertyName("deleted_at")
    @Contextual
    val deletedAt: Timestamp? = null,
    @SerialName("completed_at")
    @JvmField
    @PropertyName("completed_at")
    @Contextual
    val completedAt: Timestamp? = null
) {
    fun toDomain() =
        OrderGroup(
            uuid = uuid.validateUUID(),
            userUUID = userUUID ?: "",
            createdBy = createdBy ?: "",
            orders = orders?.map { it.value.toDomain() } ?: emptyList(),
            taxes = taxes?.map { it.value.toDomain() } ?: emptyList(),
            discounts = discounts?.map { it.value.toDomain() } ?: emptyList(),
            createdAt = createdAt?.toKtxInstant() ?: Instant.DISTANT_PAST,
            deletedAt = deletedAt?.toKtxInstant(),
            completedAt = completedAt?.toKtxInstant()
        )

    companion object {
        fun fromDomain(domain: OrderGroup) =
            OrderGroupDto(
                uuid = domain.uuid.toString(),
                userUUID = domain.userUUID,
                createdBy = domain.createdBy,
                orders =
                    domain.orders
                        .associate { it.uuid.toString() to OrderDto.fromDomain(it) },
                taxes =
                    domain.taxes
                        .associate { it.uuid.toString() to OrderTaxDto.fromDomain(it) },
                discounts =
                    domain.discounts
                        .associate { it.uuid.toString() to OrderDiscountDto.fromDomain(it) },
                createdAt = domain.createdAt.toTimestamp(),
                deletedAt = domain.deletedAt?.toTimestamp(),
                completedAt = domain.completedAt?.toTimestamp()
            )
    }
}