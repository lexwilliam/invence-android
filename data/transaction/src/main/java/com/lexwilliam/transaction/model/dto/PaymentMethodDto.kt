package com.lexwilliam.transaction.model.dto

import com.lexwilliam.core.util.validateUUID
import com.lexwilliam.transaction.model.PaymentMethod
import com.lexwilliam.transaction.model.PaymentMethodFee

data class PaymentMethodDto(
    val uuid: String? = null,
    val group: String? = null,
    val name: String? = null,
    val fee: PaymentMethodFeeDto? = null
) {
    fun toDomain(): PaymentMethod = PaymentMethod(
        uuid = uuid.validateUUID(),
        group = group ?: "",
        name = name ?: "",
        fee = fee?.toDomain()
    )

    companion object {
        fun fromDomain(domain: PaymentMethod) = PaymentMethodDto(
            uuid = domain.uuid.toString(),
            group = domain.group,
            name = domain.name,
            fee = PaymentMethodFee.fromDomain(fee)
        )
    }
}