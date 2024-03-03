package com.lexwilliam.transaction.usecase

import arrow.core.Either
import com.lexwilliam.transaction.model.Transaction
import com.lexwilliam.transaction.repository.TransactionRepository
import com.lexwilliam.transaction.util.DeleteTransactionFailure
import javax.inject.Inject

class DeleteTransactionUseCase
    @Inject
    constructor(
        private val repository: TransactionRepository
    ) {
        suspend operator fun invoke(
            transaction: Transaction
        ): Either<DeleteTransactionFailure, Transaction> {
            return repository.deleteTransaction(transaction)
        }
    }