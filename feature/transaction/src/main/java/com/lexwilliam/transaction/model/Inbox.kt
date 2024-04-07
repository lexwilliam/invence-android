package com.lexwilliam.transaction.model

import com.lexwilliam.log.model.DataLog
import kotlinx.datetime.Instant
import java.util.UUID

sealed class Inbox(
    val uuid: UUID,
    val createdAt: Instant
) {
    data class InboxTransaction(
        val transaction: Transaction
    ) : Inbox(transaction.uuid, transaction.createdAt)

    data class InboxLog(
        val log: DataLog
    ) : Inbox(log.uuid, log.createdAt)
}