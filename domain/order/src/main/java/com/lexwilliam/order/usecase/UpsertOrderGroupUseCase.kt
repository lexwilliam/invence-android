package com.lexwilliam.order.usecase

import arrow.core.Either
import com.lexwilliam.order.model.OrderGroup
import com.lexwilliam.order.repository.OrderRepository
import com.lexwilliam.order.util.UpsertGroupFailure
import javax.inject.Inject

class UpsertOrderGroupUseCase
    @Inject
    constructor(
        private val repository: OrderRepository
    ) {
        suspend operator fun invoke(order: OrderGroup): Either<UpsertGroupFailure, OrderGroup> {
            return repository.upsertOrderGroup(order)
        }
    }