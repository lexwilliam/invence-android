package com.lexwilliam.transaction.model

import java.util.UUID

data class PaymentMethod(
    val uuid: UUID,
    val group: String,
    val name: String,
    val fee: PaymentMethodFee?
)