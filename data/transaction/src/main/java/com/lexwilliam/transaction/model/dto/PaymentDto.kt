package com.lexwilliam.transaction.model.dto

import com.google.firebase.Timestamp
import com.lexwilliam.core.util.validateUUID
import com.lexwilliam.firebase.toKtxInstant
import com.lexwilliam.firebase.toTimestamp
import com.lexwilliam.transaction.model.Payment
import com.lexwilliam.transaction.model.PaymentMethod
import kotlinx.datetime.Instant

data class PaymentDto(
    val uuid: String? = null,
    val total: Double? = null,
    val paymentMethod: PaymentMethod,
    val createdAt: Timestamp? = null,
    val deletedAt: Timestamp? = null
) {
    fun toDomain(): Payment = Payment(
        uuid = uuid.validateUUID(),
        total= total ?: 0.0,
        paymentMethod = paymentMethod,
        createdAt = createdAt?.toKtxInstant() ?: Instant.DISTANT_PAST,
        deletedAt = deletedAt?.toKtxInstant()
    )

    companion object {
        fun fromDomain(domain: Payment) = PaymentDto(
            uuid = domain.uuid.toString(),
            total = domain.total,
            paymentMethod = domain.paymentMethod,
            createdAt = domain.createdAt.toTimestamp(),
            deletedAt = domain.deletedAt?.toTimestamp()
        )
    }
}