package com.lexwilliam.transaction.repository

import androidx.paging.PagingData
import arrow.core.Either
import com.lexwilliam.order.model.OrderGroup
import com.lexwilliam.order.util.CheckoutFailure
import com.lexwilliam.transaction.model.Transaction
import com.lexwilliam.transaction.util.DeleteTransactionFailure
import com.lexwilliam.transaction.util.UpsertTransactionFailure
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface TransactionRepository {
    fun observeTransaction(limit: Int?): Flow<List<Transaction>>

    fun observePagedTransaction(): Flow<PagingData<Transaction>>

    fun observeSingleTransaction(uuid: UUID): Flow<Transaction?>

    suspend fun upsertTransaction(
        transaction: Transaction
    ): Either<UpsertTransactionFailure, Transaction>

    suspend fun deleteTransaction(
        transaction: Transaction
    ): Either<DeleteTransactionFailure, Transaction>

    suspend fun checkout(order: OrderGroup): Either<CheckoutFailure, String>
}