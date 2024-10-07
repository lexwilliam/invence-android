package com.lexwilliam.transaction.model.dto

import com.google.firebase.firestore.PropertyName

data class TransactionSummaryDto(
    val uuid: String? = null,
    @JvmField @PropertyName("branch_uuid")
    val branchUUID: String? = null,
    val summaries: Map<String, TransactionDaySummaryDto>? = null,
    val date: String? = null
)