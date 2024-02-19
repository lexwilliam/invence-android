package com.lexwilliam.order.repository

import arrow.core.Either
import com.lexwilliam.order.model.OrderGroup
import com.lexwilliam.order.util.DeleteGroupFailure
import com.lexwilliam.order.util.UpsertGroupFailure
import kotlinx.coroutines.flow.Flow
import java.util.UUID

fun firebaseOrderRepository() =
    object : OrderRepository {
        override fun observeOrderGroup(branchUUID: UUID): Flow<List<OrderGroup>> {
            TODO("Not yet implemented")
        }

        override fun observeSingleOrderGroup(uuid: UUID): Flow<OrderGroup?> {
            TODO("Not yet implemented")
        }

        override suspend fun upsertOrderGroup(
            order: OrderGroup
        ): Either<UpsertGroupFailure, OrderGroup> {
            TODO("Not yet implemented")
        }

        override suspend fun deleteOrderGroup(
            order: OrderGroup
        ): Either<DeleteGroupFailure, OrderGroup> {
            TODO("Not yet implemented")
        }
    }