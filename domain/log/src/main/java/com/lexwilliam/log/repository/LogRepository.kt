package com.lexwilliam.log.repository

import arrow.core.Either
import com.lexwilliam.log.model.DataLog
import com.lexwilliam.log.util.DeleteLogFailure
import com.lexwilliam.log.util.UpsertLogFailure
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface LogRepository {
    fun observeLog(
        branchUUID: UUID,
        limit: Int?
    ): Flow<List<DataLog>>

    suspend fun upsertLog(log: DataLog): Either<UpsertLogFailure, DataLog>

    suspend fun deleteLog(log: DataLog): Either<DeleteLogFailure, DataLog>
}