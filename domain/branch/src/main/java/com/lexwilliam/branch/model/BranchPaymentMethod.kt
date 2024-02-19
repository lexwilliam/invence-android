package com.lexwilliam.branch.model

import java.util.UUID

data class BranchPaymentMethod(
    val uuid: UUID,
    val group: String,
    val name: String,
    val fee: BranchPaymentMethodFee?
)