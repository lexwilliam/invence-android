package com.lexwilliam.log.usecase

import arrow.core.Either
import com.lexwilliam.log.model.DataLog
import com.lexwilliam.log.repository.LogRepository
import com.lexwilliam.log.util.UpsertLogFailure
import javax.inject.Inject

class UpsertLogUseCase
    @Inject
    constructor(
        private val repository: LogRepository
    ) {
        suspend operator fun invoke(log: DataLog): Either<UpsertLogFailure, DataLog> {
            return repository.upsertLog(log)
        }
    }