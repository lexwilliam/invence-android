package com.lexwilliam.transaction.usecase

import androidx.paging.PagingData
import com.lexwilliam.transaction.model.Transaction
import com.lexwilliam.transaction.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveTransactionUseCase
    @Inject
    constructor(
        private val repository: TransactionRepository
    ) {
        operator fun invoke(): Flow<PagingData<Transaction>> {
            return repository.observePagedTransaction()
        }
    }