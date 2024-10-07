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
    val customer: String? = null,
    val total: Double? = null,
    val profit: Double? = null,
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
                branchUUID = domain.branchUUID.toString(),
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