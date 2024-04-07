package com.lexwilliam.home.model

import com.lexwilliam.log.model.DataLog
import com.lexwilliam.transaction.model.Transaction
import kotlinx.datetime.Instant

sealed class Inbox(val createdAt: Instant) {
    data class InboxTransaction(
        val transaction: Transaction
    ) : Inbox(transaction.createdAt)

    data class InboxLog(val log: DataLog) : Inbox(log.createdAt)
}