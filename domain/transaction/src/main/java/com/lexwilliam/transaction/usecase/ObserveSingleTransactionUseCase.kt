package com.lexwilliam.transaction.usecase

import com.lexwilliam.transaction.model.Transaction
import com.lexwilliam.transaction.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject

class ObserveSingleTransactionUseCase
    @Inject
    constructor(
        private val repository: TransactionRepository
    ) {
        operator fun invoke(uuid: UUID): Flow<Transaction?> {
            return repository.observeSingleTransaction(uuid)
        }
    }