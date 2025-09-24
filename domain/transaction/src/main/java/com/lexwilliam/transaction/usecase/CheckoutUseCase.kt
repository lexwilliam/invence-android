package com.lexwilliam.transaction.usecase

import arrow.core.Either
import com.lexwilliam.order.util.CheckoutFailure
import com.lexwilliam.transaction.repository.TransactionRepository
import java.util.UUID
import javax.inject.Inject

class CheckoutUseCase
    @Inject
    constructor(
        private val repository: TransactionRepository
    ) {
        suspend operator fun invoke(orderId: UUID): Either<CheckoutFailure, String> {
            return repository.checkout(orderId)
        }
    }