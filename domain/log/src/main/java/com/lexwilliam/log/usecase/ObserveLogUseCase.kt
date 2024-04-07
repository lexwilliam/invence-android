package com.lexwilliam.log.usecase

import com.lexwilliam.log.model.DataLog
import com.lexwilliam.log.repository.LogRepository
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject

class ObserveLogUseCase
    @Inject
    constructor(
        private val repository: LogRepository
    ) {
        operator fun invoke(
            branchUUID: UUID,
            limit: Int? = null
        ): Flow<List<DataLog>> = repository.observeLog(branchUUID, limit)
    }