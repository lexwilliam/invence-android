package com.lexwilliam.branch.model

import com.lexwilliam.core.util.validateUUID

data class PaymentMethodDto(
    val uuid: String? = null,
    val group: String? = null,
    val name: String? = null,
    val fee: PaymentMethodFeeDto? = null
) {
    fun toDomain(): BranchPaymentMethod =
        BranchPaymentMethod(
            uuid = uuid.validateUUID(),
            group = group ?: "",
            name = name ?: "",
            fee = fee?.toDomain()
        )

    companion object {
        fun fromDomain(domain: BranchPaymentMethod) =
            PaymentMethodDto(
                uuid = domain.uuid.toString(),
                group = domain.group,
                name = domain.name,
                fee = domain.fee?.let { PaymentMethodFeeDto.fromDomain(it) }
            )
    }
}