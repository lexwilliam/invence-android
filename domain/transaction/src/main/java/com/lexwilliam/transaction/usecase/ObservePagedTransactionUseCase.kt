package com.lexwilliam.transaction.usecase

import com.lexwilliam.transaction.repository.TransactionRepository
import javax.inject.Inject

class ObservePagedTransactionUseCase
    @Inject
    constructor(
        private val repository: TransactionRepository
    ) {
        operator fun invoke() = repository.observePagedTransaction()
    }