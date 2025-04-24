package com.lexwilliam.order.checkout.dialog

import com.lexwilliam.transaction.model.Transaction

data class OrderSuccessDialogState(
    val transaction: Transaction
)