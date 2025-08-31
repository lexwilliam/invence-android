package com.lexwilliam.transaction.model.dto

import com.google.firebase.firestore.PropertyName

data class TransactionSummaryDto(
    val uuid: String? = null,
    @JvmField @PropertyName("user_uuid")
    val userUUID: String? = null,
    val summaries: Map<String, TransactionDaySummaryDto>? = null,
    val date: String? = null
)