package com.lexwilliam.transaction.repository

import arrow.core.Either
import arrow.core.raise.catch
import arrow.core.raise.either
import com.google.firebase.Timestamp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.FirebaseFirestore
import com.lexwilliam.firebase.FirestoreConfig
import com.lexwilliam.transaction.model.Transaction
import com.lexwilliam.transaction.model.dto.TransactionDto
import com.lexwilliam.transaction.util.DeleteTransactionFailure
import com.lexwilliam.transaction.util.UpsertTransactionFailure
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.UUID

fun firebaseTransactionRepository(
    store: FirebaseFirestore,
    analytics: FirebaseCrashlytics
) = object : TransactionRepository {
    override fun observeTransaction(branchUUID: UUID): Flow<List<Transaction>> =
        callbackFlow {
            val reference =
                store
                    .collection(FirestoreConfig.COLLECTION_TRANSACTION)
                    .whereEqualTo(FirestoreConfig.Field.BRANCH_UUID, branchUUID.toString())
                    .whereEqualTo(FirestoreConfig.Field.DELETED_AT, null)

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

    override fun observeSingleTransaction(uuid: UUID): Flow<Transaction?> =
        callbackFlow {
            val reference =
                store
                    .collection(FirestoreConfig.COLLECTION_PRODUCT_CATEGORY)
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

    override suspend fun upsertTransaction(transaction: Transaction): Either<UpsertTransactionFailure, Transaction> =
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

    override suspend fun deleteTransaction(transaction: Transaction): Either<DeleteTransactionFailure, Transaction> =
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

}