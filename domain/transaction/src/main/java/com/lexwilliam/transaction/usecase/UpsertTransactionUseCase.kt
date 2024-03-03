package com.lexwilliam.transaction.usecase

import arrow.core.Either
import com.lexwilliam.transaction.model.Transaction
import com.lexwilliam.transaction.repository.TransactionRepository
import com.lexwilliam.transaction.util.UpsertTransactionFailure
import javax.inject.Inject

class UpsertTransactionUseCase
    @Inject
    constructor(
        private val repository: TransactionRepository
    ) {
        suspend operator fun invoke(
            transaction: Transaction
        ): Either<UpsertTransactionFailure, Transaction> {
            return repository.upsertTransaction(transaction)
        }
    }