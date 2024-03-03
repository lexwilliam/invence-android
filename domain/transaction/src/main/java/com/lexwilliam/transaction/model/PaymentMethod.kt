package com.lexwilliam.transaction.model

import java.util.UUID

data class PaymentMethod(
    val uuid: UUID = UUID.randomUUID(),
    val group: String = "",
    val name: String = "",
    val fee: PaymentMethodFee? = null
)