package com.lexwilliam.log.repository

import arrow.core.Either
import arrow.core.raise.catch
import arrow.core.raise.either
import com.google.firebase.Timestamp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.FirebaseFirestore
import com.lexwilliam.firebase.FirestoreConfig
import com.lexwilliam.log.model.DataLog
import com.lexwilliam.log.model.dto.LogDto
import com.lexwilliam.log.util.DeleteLogFailure
import com.lexwilliam.log.util.UpsertLogFailure
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.UUID

fun firebaseLogRepository(
    analytics: FirebaseCrashlytics,
    store: FirebaseFirestore
) = object : LogRepository {
    override fun observeLog(
        branchUUID: UUID,
        limit: Int?
    ): Flow<List<DataLog>> =
        callbackFlow {
            var reference =
                store
                    .collection(FirestoreConfig.COLLECTION_LOG)
                    .whereEqualTo(FirestoreConfig.Field.BRANCH_UUID, branchUUID.toString())
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
                            ?.toObjects(LogDto::class.java)
                            ?.map { log -> log.toDomain() } ?: emptyList()
                    )
                }

            awaitClose { registration.remove() }
        }

    override suspend fun upsertLog(log: DataLog): Either<UpsertLogFailure, DataLog> =
        either {
            catch({
                store
                    .collection(FirestoreConfig.COLLECTION_LOG)
                    .document(log.uuid.toString())
                    .set(LogDto.fromDomain(log))
            }) { t ->
                t.printStackTrace()
                analytics.recordException(t)
                UnknownError(t.message)
            }
            log
        }

    override suspend fun deleteLog(log: DataLog): Either<DeleteLogFailure, DataLog> =
        either {
            catch({
                store
                    .collection(FirestoreConfig.COLLECTION_LOG)
                    .document(log.uuid.toString())
                    .update(FirestoreConfig.Field.DELETED_AT, Timestamp.now())
            }) { t ->
                t.printStackTrace()
                analytics.recordException(t)
                UnknownError(t.message)
            }
            log
        }
}