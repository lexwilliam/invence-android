package com.lexwilliam.transaction.model.dto

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName
import com.lexwilliam.core.util.validateUUID
import com.lexwilliam.firebase.toKtxInstant
import com.lexwilliam.firebase.toTimestamp
import com.lexwilliam.transaction.model.Refund
import kotlinx.datetime.Instant

data class RefundDto(
    val uuid: String? = null,
    val total: Double? = null,
    @JvmField @PropertyName("refunded_by")
    val refundedBy: String? = null,
    val reason: String? = null,
    @JvmField @PropertyName("created_at")
    val createdAt: Timestamp? = null,
    @JvmField @PropertyName("deleted_at")
    val deletedAt: Timestamp? = null
) {
    fun toDomain() =
        Refund(
            uuid = uuid.validateUUID(),
            total = total ?: 0.0,
            refundedBy = refundedBy ?: "",
            reason = reason ?: "",
            createdAt = createdAt?.toKtxInstant() ?: Instant.DISTANT_PAST,
            deletedAt = deletedAt?.toKtxInstant()
        )

    companion object {
        fun fromDomain(domain: Refund) =
            RefundDto(
                uuid = domain.uuid.toString(),
                total = domain.total,
                refundedBy = domain.refundedBy,
                reason = domain.reason,
                createdAt = domain.createdAt.toTimestamp(),
                deletedAt = domain.deletedAt?.toTimestamp()
            )
    }
}