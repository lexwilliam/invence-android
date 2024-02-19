package com.lexwilliam.order.usecase

import com.lexwilliam.order.model.OrderGroup
import com.lexwilliam.order.repository.OrderRepository
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject

class ObserveOrderGroupUseCase
    @Inject
    constructor(
        private val repository: OrderRepository
    ) {
        operator fun invoke(branchUUID: UUID): Flow<List<OrderGroup>> {
            return repository.observeOrderGroup(branchUUID)
        }
    }