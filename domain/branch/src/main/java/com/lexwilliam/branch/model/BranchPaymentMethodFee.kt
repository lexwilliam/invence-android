package com.lexwilliam.branch.model

data class BranchPaymentMethodFee(
    val fixed: Double,
    val percent: Float
) {
    fun calculate(total: Double): Double {
        return total + fixed + (total * percent)
    }
}