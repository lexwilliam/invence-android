package com.lexwilliam.transaction.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import arrow.core.Either
import arrow.core.raise.catch
import arrow.core.raise.either
import com.google.firebase.Timestamp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.functions.FirebaseFunctions
import com.lexwilliam.core.session.ObserveSessionUseCase
import com.lexwilliam.firebase.utils.FirestoreConfig
import com.lexwilliam.order.util.CheckoutFailure
import com.lexwilliam.transaction.model.Transaction
import com.lexwilliam.transaction.model.dto.TransactionDto
import com.lexwilliam.transaction.source.TransactionPagingSource
import com.lexwilliam.transaction.util.DeleteTransactionFailure
import com.lexwilliam.transaction.util.UpsertTransactionFailure
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.json.Json
import java.util.UUID

@OptIn(ExperimentalCoroutinesApi::class)
fun firebaseTransactionRepository(
    observeSession: ObserveSessionUseCase,
    store: FirebaseFirestore,
    analytics: FirebaseCrashlytics,
    functions: FirebaseFunctions,
    json: Json
) = object : TransactionRepository {
    val userUUID = observeSession().map { it.getUserId() }

    override fun observeTransaction(limit: Int?): Flow<List<Transaction>> =
        callbackFlow {
            val userUUID = userUUID.firstOrNull()
            if (userUUID == null) trySend(emptyList())

            var reference =
                store
                    .collection(FirestoreConfig.COLLECTION_TRANSACTION)
                    .whereEqualTo(FirestoreConfig.Field.USER_UUID, userUUID.toString())
                    .whereEqualTo(FirestoreConfig.Field.DELETED_AT, null)

            if (limit != null) reference = reference.limit(limit.toLong())

            val registration =
                reference.addSnapshotListener { value, error ->
                    error?.let { exception ->
                        analytics.recordException(exception)
                        trySend(emptyList())
                    }
                    trySend(
                        value
                            ?.toObjects(TransactionDto::class.java)
                            ?.map { it.toDomain() } ?: emptyList()
                    )
                }

            awaitClose { registration.remove() }
        }

    override fun observePagedTransaction(): Flow<PagingData<Transaction>> {
        return userUUID
            .map { it ?: return@map null } // emit null if no user
            .distinctUntilChanged()
            .flatMapLatest { uuid ->
                if (uuid == null) {
                    flowOf(PagingData.empty())
                } else {
                    val query =
                        store
                            .collection(FirestoreConfig.COLLECTION_TRANSACTION)
                            .whereEqualTo(FirestoreConfig.Field.USER_UUID, uuid)
                            .whereEqualTo(FirestoreConfig.Field.DELETED_AT, null)
                            .orderBy(FirestoreConfig.Field.CREATED_AT, Query.Direction.DESCENDING)

                    Pager(
                        config = PagingConfig(pageSize = 20),
                        pagingSourceFactory = { TransactionPagingSource(query) }
                    ).flow
                }
            }
    }

    override fun observeSingleTransaction(uuid: UUID): Flow<Transaction?> =
        callbackFlow {
            val reference =
                store
                    .collection(FirestoreConfig.COLLECTION_TRANSACTION)
                    .document(uuid.toString())

            val registration =
                reference.addSnapshotListener { value, error ->
                    error?.let { exception ->
                        analytics.recordException(exception)
                        trySend(null)
                    }
                    trySend(
                        value
                            ?.toObject(TransactionDto::class.java)
                            ?.toDomain()
                    )
                }

            awaitClose { registration.remove() }
        }

    override suspend fun upsertTransaction(
        transaction: Transaction
    ): Either<UpsertTransactionFailure, Transaction> =
        either {
            catch({
                store
                    .collection(FirestoreConfig.COLLECTION_TRANSACTION)
                    .document(transaction.uuid.toString())
                    .set(TransactionDto.fromDomain(transaction))
            }) { t ->
                t.printStackTrace()
                analytics.recordException(t)
                UnknownError(t.message)
            }
            transaction
        }

    override suspend fun deleteTransaction(
        transaction: Transaction
    ): Either<DeleteTransactionFailure, Transaction> =
        either {
            catch({
                store
                    .collection(FirestoreConfig.COLLECTION_TRANSACTION)
                    .document(transaction.uuid.toString())
                    .update(FirestoreConfig.Field.DELETED_AT, Timestamp.now())
            }) { t ->
                t.printStackTrace()
                analytics.recordException(t)
                UnknownError(t.message)
            }
            transaction
        }

    override suspend fun checkout(orderId: UUID): Either<CheckoutFailure, String> {
        val request = mapOf("orderId" to orderId.toString())
        return Either.catch {
            val response =
                functions
                    .getHttpsCallable("checkout")
                    .call(request)
                    .await()
            val data = response.data
            if (data == null) {
                return Either.Left(CheckoutFailure.UnknownFailure("Checkout Failed"))
            }

            // Firebase Functions returns a Map<String, Any>, not a JSON string
            val responseMap = data as? Map<String, Any>
            if (responseMap == null) {
                return Either.Left(CheckoutFailure.UnknownFailure("Invalid response format"))
            }

            val success = responseMap["success"] as? Boolean ?: false
            val message = responseMap["message"] as? String ?: "Unknown error"
            val transactionId = responseMap["data"] as? String

            if (success) {
                if (transactionId == null) {
                    return Either.Left(CheckoutFailure.UnknownFailure("Checkout Failed"))
                }
                return Either.Right(transactionId)
            } else {
                return Either.Left(CheckoutFailure.UnknownFailure(message))
            }
        }.mapLeft { t ->
            t.printStackTrace()
            analytics.recordException(t)
            CheckoutFailure.UnknownFailure(t.message)
        }
    }
}