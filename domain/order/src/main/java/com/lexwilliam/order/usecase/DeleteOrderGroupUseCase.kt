package com.lexwilliam.order.usecase

import arrow.core.Either
import com.lexwilliam.order.model.OrderGroup
import com.lexwilliam.order.repository.OrderRepository
import com.lexwilliam.order.util.DeleteGroupFailure
import javax.inject.Inject

class DeleteOrderGroupUseCase
    @Inject
    constructor(
        private val repository: OrderRepository
    ) {
        suspend operator fun invoke(order: OrderGroup): Either<DeleteGroupFailure, OrderGroup> {
            return repository.deleteOrderGroup(order)
        }
    }