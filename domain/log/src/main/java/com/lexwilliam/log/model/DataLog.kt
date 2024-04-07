package com.lexwilliam.log.model

import kotlinx.datetime.Instant
import java.util.UUID

data class DataLog(
    val uuid: UUID,
    val branchUUID: UUID,
    val restock: LogRestock? = null,
    val sell: LogSell? = null,
    val update: LogUpdate? = null,
    val add: LogAdd? = null,
    val delete: LogDelete? = null,
    val createdAt: Instant,
    val deletedAt: Instant? = null
)