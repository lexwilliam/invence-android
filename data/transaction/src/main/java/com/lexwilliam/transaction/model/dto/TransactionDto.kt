package com.lexwilliam.transaction.model.dto

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName
import com.lexwilliam.core.util.validateUUID
import com.lexwilliam.firebase.extensions.toKtxInstant
import com.lexwilliam.firebase.extensions.toTimestamp
import com.lexwilliam.order.model.dto.OrderGroupDto
import com.lexwilliam.transaction.model.Transaction
import kotlinx.datetime.Instant
import kotlinx.serialization.Contextual
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@OptIn(InternalSerializationApi::class)
@Serializable
data class TransactionDto(
    val uuid: String? = null,
    @SerialName("user_uuid")
    @JvmField
    @PropertyName("user_uuid")
    val userUUID: String? = null,
    @SerialName("order_group")
    @JvmField
    @PropertyName("order_group")
    val orderGroup: OrderGroupDto? = null,
    val customer: String? = null,
    val total: Double? = null,
    val profit: Double? = null,
    @SerialName("created_by")
    @JvmField
    @PropertyName("created_by")
    val createdBy: String? = null,
    @SerialName("created_at")
    @JvmField
    @PropertyName("created_at")
    @Contextual
    val createdAt: Timestamp? = null,
    @SerialName("deleted_at")
    @JvmField
    @PropertyName("deleted_at")
    @Contextual
    val deletedAt: Timestamp? = null
) {
    fun toDomain() =
        Transaction(
            uuid = uuid.validateUUID(),
            userUUID = userUUID ?: "",
            orderGroup = orderGroup?.toDomain() ?: OrderGroupDto().toDomain(),
            customer = customer ?: "",
            total = total ?: 0.0,
            profit = profit ?: 0.0,
            createdBy = createdBy ?: "",
            createdAt = createdAt?.toKtxInstant() ?: Instant.DISTANT_PAST,
            deletedAt = deletedAt?.toKtxInstant()
        )

    companion object {
        fun fromDomain(domain: Transaction) =
            TransactionDto(
                uuid = domain.uuid.toString(),
                userUUID = domain.userUUID,
                orderGroup = OrderGroupDto.fromDomain(domain.orderGroup),
                customer = domain.customer,
                total = domain.total,
                profit = domain.profit,
                createdBy = domain.createdBy,
                createdAt = domain.createdAt.toTimestamp(),
                deletedAt = domain.deletedAt?.toTimestamp()
            )
    }
}