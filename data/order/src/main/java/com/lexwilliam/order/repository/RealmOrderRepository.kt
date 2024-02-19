package com.lexwilliam.order.repository

import arrow.core.Either
import com.lexwilliam.order.model.OrderGroup
import com.lexwilliam.order.source.OrderLocalSource
import com.lexwilliam.order.util.DeleteGroupFailure
import com.lexwilliam.order.util.UnknownFailure
import com.lexwilliam.order.util.UpsertGroupFailure
import kotlinx.coroutines.flow.Flow
import java.util.UUID

fun realmOrderRepository(source: OrderLocalSource) =
    object : OrderRepository {
        override fun observeOrderGroup(branchUUID: UUID): Flow<List<OrderGroup>> {
            return source.observeOrderGroup(branchUUID)
        }

        override fun observeSingleOrderGroup(uuid: UUID): Flow<OrderGroup?> {
            return source.observeSingleOrderGroup(uuid)
        }

        override suspend fun upsertOrderGroup(
            order: OrderGroup
        ): Either<UpsertGroupFailure, OrderGroup> {
            return source.upsertOrderGroup(order).mapLeft { message ->
                UnknownFailure(message)
            }
        }

        override suspend fun deleteOrderGroup(
            order: OrderGroup
        ): Either<DeleteGroupFailure, OrderGroup> {
            return source.deleteOrderGroup(order).mapLeft { message ->
                UnknownFailure(message)
            }
        }
    }