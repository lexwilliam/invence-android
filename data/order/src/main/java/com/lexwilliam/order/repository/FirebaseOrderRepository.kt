package com.lexwilliam.order.repository

import arrow.core.Either
import com.google.firebase.Timestamp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.FirebaseFirestore
import com.lexwilliam.core.session.ObserveSessionUseCase
import com.lexwilliam.firebase.utils.FirestoreConfig
import com.lexwilliam.order.model.OrderGroup
import com.lexwilliam.order.model.dto.OrderGroupDto
import com.lexwilliam.order.util.DeleteGroupFailure
import com.lexwilliam.order.util.UnknownFailure
import com.lexwilliam.order.util.UpsertGroupFailure
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.util.UUID

fun firebaseOrderRepository(
    observeSession: ObserveSessionUseCase,
    analytics: FirebaseCrashlytics,
    store: FirebaseFirestore
) = object : OrderRepository {
    val userUUID = observeSession().map { it.getUserId() }

    override fun observeOrderGroup(): Flow<List<OrderGroup>> =
        callbackFlow {
            val userUUID = userUUID.firstOrNull()
            if (userUUID == null) trySend(emptyList())

            val reference =
                store
                    .collection(FirestoreConfig.COLLECTION_ORDER)
                    .whereEqualTo(FirestoreConfig.Field.USER_UUID, userUUID.toString())
                    .whereEqualTo(FirestoreConfig.Field.DELETED_AT, null)

            val registration =
                reference.addSnapshotListener { value, error ->
                    error?.let { exception ->
                        analytics.recordException(exception)
                        trySend(emptyList())
                    }
                    trySend(
                        value
                            ?.toObjects(OrderGroupDto::class.java)
                            ?.map { orderGroup -> orderGroup.toDomain() } ?: emptyList()
                    )
                }

            awaitClose { registration.remove() }
        }

    override fun observeSingleOrderGroup(uuid: UUID): Flow<OrderGroup?> =
        callbackFlow {
            val reference =
                store
                    .collection(FirestoreConfig.COLLECTION_ORDER)
                    .document(uuid.toString())

            val registration =
                reference.addSnapshotListener { value, error ->
                    error?.let { exception ->
                        analytics.recordException(exception)
                        trySend(null)
                    }
                    trySend(
                        value
                            ?.toObject(OrderGroupDto::class.java)
                            ?.toDomain()
                    )
                }

            awaitClose { registration.remove() }
        }

    override suspend fun upsertOrderGroup(
        order: OrderGroup
    ): Either<UpsertGroupFailure, OrderGroup> {
        return Either.catch {
            store
                .collection(FirestoreConfig.COLLECTION_ORDER)
                .document(order.uuid.toString())
                .set(OrderGroupDto.fromDomain(order))
            order
        }.mapLeft { t ->
            t.printStackTrace()
            analytics.recordException(t)
            UnknownFailure(t.message)
        }
    }

    override suspend fun deleteOrderGroup(
        order: OrderGroup
    ): Either<DeleteGroupFailure, OrderGroup> {
        return Either.catch {
            store
                .collection(FirestoreConfig.COLLECTION_ORDER)
                .document(order.uuid.toString())
                .update(FirestoreConfig.Field.DELETED_AT, Timestamp.now())
            order
        }.mapLeft { t ->
            t.printStackTrace()
            analytics.recordException(t)
            UnknownFailure(t.message)
        }
    }
}