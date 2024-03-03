package com.lexwilliam.transaction.usecase

import com.lexwilliam.transaction.model.Transaction
import com.lexwilliam.transaction.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject

class ObserveTransactionUseCase
    @Inject
    constructor(
        private val repository: TransactionRepository
    ) {
        operator fun invoke(
            branchUUID: UUID,
            limit: Int? = null
        ): Flow<List<Transaction>> {
            return repository.observeTransaction(branchUUID, limit)
        }
    }