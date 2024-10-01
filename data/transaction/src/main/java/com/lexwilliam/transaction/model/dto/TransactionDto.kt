package com.lexwilliam.transaction.model.dto

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName
import com.lexwilliam.core.util.validateUUID
import com.lexwilliam.firebase.toKtxInstant
import com.lexwilliam.firebase.toTimestamp
import com.lexwilliam.order.model.dto.OrderGroupDto
import com.lexwilliam.transaction.model.Transaction
import kotlinx.datetime.Instant

data class TransactionDto(
    val uuid: String? = null,
    @JvmField @PropertyName("branch_uuid")
    val branchUUID: String? = null,
    @JvmField @PropertyName("order_group")
    val orderGroup: OrderGroupDto? = null,
    val profit: Double? = null,
    val customer: String? = null,
    @JvmField @PropertyName("created_by")
    val createdBy: String? = null,
    @JvmField @PropertyName("created_at")
    val createdAt: Timestamp? = null,
    @JvmField @PropertyName("deleted_at")
    val deletedAt: Timestamp? = null
) {
    fun toDomain() =
        Transaction(
            uuid = uuid.validateUUID(),
            branchUUID = branchUUID.validateUUID(),
            orderGroup = orderGroup?.toDomain() ?: OrderGroupDto().toDomain(),
            profit = profit ?: 0.0,
            customer = customer ?: "",
            createdBy = createdBy ?: "",
            createdAt = createdAt?.toKtxInstant() ?: Instant.DISTANT_PAST,
            deletedAt = deletedAt?.toKtxInstant()
        )

    companion object {
        fun fromDomain(domain: Transaction) =
            TransactionDto(
                uuid = domain.uuid.toString(),
                branchUUID = domain.branchUUID.toString(),
                orderGroup = OrderGroupDto.fromDomain(domain.orderGroup),
                profit = domain.profit,
                customer = domain.customer,
                createdBy = domain.createdBy,
                createdAt = domain.createdAt.toTimestamp(),
                deletedAt = domain.deletedAt?.toTimestamp()
            )
    }
}