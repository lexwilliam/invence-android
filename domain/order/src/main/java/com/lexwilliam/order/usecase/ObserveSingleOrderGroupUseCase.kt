package com.lexwilliam.order.usecase

import com.lexwilliam.order.model.OrderGroup
import com.lexwilliam.order.repository.OrderRepository
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject

class ObserveSingleOrderGroupUseCase
    @Inject
    constructor(
        private val repository: OrderRepository
    ) {
        operator fun invoke(uuid: UUID): Flow<OrderGroup?> {
            return repository.observeSingleOrderGroup(uuid)
        }
    }