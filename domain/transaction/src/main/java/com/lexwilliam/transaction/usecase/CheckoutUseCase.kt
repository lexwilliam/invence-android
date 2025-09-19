package com.lexwilliam.transaction.usecase

import arrow.core.Either
import com.lexwilliam.order.model.OrderGroup
import com.lexwilliam.order.util.CheckoutFailure
import com.lexwilliam.transaction.repository.TransactionRepository
import javax.inject.Inject

class CheckoutUseCase
    @Inject
    constructor(
        private val repository: TransactionRepository
    ) {
        suspend operator fun invoke(order: OrderGroup): Either<CheckoutFailure, String> {
            return repository.checkout(order)
        }
    }