package com.lexwilliam.order.usecase

import com.lexwilliam.order.model.OrderGroup
import com.lexwilliam.order.repository.OrderRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveOrderGroupUseCase
    @Inject
    constructor(
        private val repository: OrderRepository
    ) {
        operator fun invoke(): Flow<List<OrderGroup>> {
            return repository.observeOrderGroup()
        }
    }