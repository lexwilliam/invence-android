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
    val payments: Map<String, PaymentDto>? = null,
    val refunds: Map<String, RefundDto>? = null,
    val customer: String? = null,
    val fee: TransactionFeeDto? = null,
    val tip: Double? = null,
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
            payments = payments?.map { it.value.toDomain() } ?: emptyList(),
            refunds = refunds?.map { it.value.toDomain() } ?: emptyList(),
            customer = customer ?: "",
            fee = fee?.toDomain(),
            tip = tip ?: 0.0,
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
                payments =
                    domain.payments
                        .associate { it.uuid.toString() to PaymentDto.fromDomain(it) },
                refunds =
                    domain.refunds
                        .associate { it.uuid.toString() to RefundDto.fromDomain(it) },
                customer = domain.customer,
                fee = domain.fee?.let { TransactionFeeDto.fromDomain(it) },
                tip = domain.tip,
                createdBy = domain.createdBy,
                createdAt = domain.createdAt.toTimestamp(),
                deletedAt = domain.deletedAt?.toTimestamp()
            )
    }
}