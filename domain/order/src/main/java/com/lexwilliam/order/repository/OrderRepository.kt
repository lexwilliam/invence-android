package com.lexwilliam.order.repository

import arrow.core.Either
import com.lexwilliam.order.model.OrderGroup
import com.lexwilliam.order.util.DeleteGroupFailure
import com.lexwilliam.order.util.UpsertGroupFailure
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface OrderRepository {
    fun observeOrderGroup(branchUUID: UUID): Flow<List<OrderGroup>>

    fun observeSingleOrderGroup(uuid: UUID): Flow<OrderGroup?>

    suspend fun upsertOrderGroup(order: OrderGroup): Either<UpsertGroupFailure, OrderGroup>

    suspend fun deleteOrderGroup(order: OrderGroup): Either<DeleteGroupFailure, OrderGroup>
}