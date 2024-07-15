package com.lexwilliam.user.repository

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.FirebaseFirestore
import com.lexwilliam.firebase.FirestoreConfig
import com.lexwilliam.user.model.EmployeeShift
import com.lexwilliam.user.model.EmployeeShiftDto
import com.lexwilliam.user.model.User
import com.lexwilliam.user.model.UserDto
import com.lexwilliam.user.util.FetchUserFailure
import com.lexwilliam.user.util.UnknownFailure
import com.lexwilliam.user.util.UpsertShiftFailure
import com.lexwilliam.user.util.UpsertUserFailure
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

fun firebaseUserRepository(
    analytics: FirebaseCrashlytics,
    store: FirebaseFirestore
) = object : UserRepository {
    override fun observeUser(uuid: String): Flow<User?> =
        callbackFlow {
            val reference =
                store
                    .collection(FirestoreConfig.COLLECTION_USER)
                    .document(uuid)

            val registration =
                reference.addSnapshotListener { value, error ->
                    error?.let { exception ->
                        analytics.recordException(exception)
                        trySend(null)
                    }

                    trySend(
                        value
                            ?.toObject(UserDto::class.java)
                            ?.toDomain()
                    )
                }

            awaitClose { registration.remove() }
        }

    override suspend fun fetchUser(uuid: String): Either<FetchUserFailure, User> {
        return Either.catch {
            val documentSnapshot =
                store
                    .collection(FirestoreConfig.COLLECTION_USER)
                    .document(uuid)
                    .get()
                    .await()

            if (documentSnapshot.exists()) {
                // The document with the given ID exists
                val user =
                    documentSnapshot.toObject(UserDto::class.java)
                        ?.toDomain()
                return user?.right() ?: FetchUserFailure.UserIsNull.left()
            } else {
                // Document not found
                return FetchUserFailure.DocumentNotFound.left()
            }
        }.mapLeft { t ->
            t.printStackTrace()
            analytics.recordException(t)
            UnknownFailure(t.message)
        }
    }

    override suspend fun upsertUser(user: User): Either<UpsertUserFailure, User> {
        return Either.catch {
            store
                .collection(FirestoreConfig.COLLECTION_USER)
                .document(user.uuid)
                .set(UserDto.fromDomain(user))
            user
        }.mapLeft { t ->
            t.printStackTrace()
            analytics.recordException(t)
            UnknownFailure(t.message)
        }
    }

    override fun observeShift(uuid: String): Flow<EmployeeShift?> =
        callbackFlow {
            val reference =
                store
                    .collection(FirestoreConfig.COLLECTION_EMPLOYEE_SHIFT)
                    .document(uuid)

            val registration =
                reference.addSnapshotListener { value, error ->
                    error?.let { exception ->
                        analytics.recordException(exception)
                        trySend(null)
                    }

                    trySend(
                        value
                            ?.toObject(EmployeeShiftDto::class.java)
                            ?.toDomain()
                    )
                }

            awaitClose { registration.remove() }
        }

    override suspend fun upsertShift(
        shift: EmployeeShift
    ): Either<UpsertShiftFailure, EmployeeShift> {
        return Either.catch {
            store
                .collection(FirestoreConfig.COLLECTION_EMPLOYEE_SHIFT)
                .document(shift.userUUID.toString())
                .set(EmployeeShiftDto.fromDomain(shift))
            shift
        }.mapLeft { t ->
            t.printStackTrace()
            analytics.recordException(t)
            UnknownFailure(t.message)
        }
    }
}