package com.lexwilliam.transaction.model.dto

data class TransactionDaySummaryDto(
    val date: String? = null,
    val expense: Double? = null,
    val profit: Double? = null,
    val total: Double? = null
)