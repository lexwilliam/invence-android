package com.lexwilliam.branch.model

data class PaymentMethodFeeDto(
    val fixed: Double? = null,
    val percent: Float? = null
) {
    fun toDomain() = BranchPaymentMethodFee(
        fixed = fixed ?: 0.0,
        percent = percent ?: 0.0f
    )

    companion object {
        fun fromDomain(domain: BranchPaymentMethodFee) = PaymentMethodFeeDto(
            fixed = domain.fixed,
            percent = domain.percent
        )
    }
}