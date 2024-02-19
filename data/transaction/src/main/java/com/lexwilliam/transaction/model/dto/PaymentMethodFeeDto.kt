package com.lexwilliam.transaction.model.dto

import com.lexwilliam.transaction.model.PaymentMethodFee

data class PaymentMethodFeeDto(
    val fixed: Double? = null,
    val percent: Float? = null
) {
    fun toDomain() = PaymentMethodFee(
        fixed = fixed ?: 0.0,
        percent = percent ?: 0.0f
    )

    companion object {
        fun fromDomain(domain: PaymentMethodFee) = PaymentMethodFeeDto(
            fixed = domain.fixed,
            percent = domain.percent
        )
    }
}