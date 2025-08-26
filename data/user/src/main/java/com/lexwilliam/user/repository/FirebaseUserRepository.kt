package com.lexwilliam.user.repository

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.FirebaseFirestore
import com.lexwilliam.firebase.utils.FirestoreConfig
import com.lexwilliam.user.model.User
import com.lexwilliam.user.model.UserDto
import com.lexwilliam.user.util.FetchUserFailure
import com.lexwilliam.user.util.LoginFailure
import com.lexwilliam.user.util.SignUpFailure
import com.lexwilliam.user.util.UnknownFailure
import com.lexwilliam.user.util.UpsertUserFailure
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.datetime.Clock

fun firebaseUserRepository(
    analytics: FirebaseCrashlytics,
    store: FirebaseFirestore,
    auth: FirebaseAuth
) = object : UserRepository {
    override suspend fun login(
        email: String,
        password: String
    ): Either<LoginFailure, User> {
        return Either.catch {
            val authResult =
                auth
                    .signInWithEmailAndPassword(email, password)
                    .await()
                    ?: return LoginFailure.TaskFailed.left()
            val firebaseUser =
                authResult.user
                    ?: return LoginFailure.TaskFailed.left()
            val user =
                fetchUser(firebaseUser.uid).getOrNull()
                    ?: return LoginFailure.UserNotFound.left()
            user
        }.mapLeft { t ->
            t.printStackTrace()
            analytics.recordException(t)
            UnknownFailure(t.message)
        }
    }

    override suspend fun signUp(
        email: String,
        username: String,
        password: String
    ): Either<SignUpFailure, User> {
        return Either.catch {
            val authResult =
                auth
                    .createUserWithEmailAndPassword(email, password)
                    .await()
            val firebaseUser =
                authResult?.user
                    ?: return SignUpFailure.CreateUserTaskFail.left()
            val user =
                User(
                    uuid = firebaseUser.uid,
                    email = email,
                    name = username,
                    createdAt = Clock.System.now(),
                    imageUrl = null
                )
            return when (upsertUser(user)) {
                is Either.Left -> SignUpFailure.UpsertUserToFirestoreFail.left()
                is Either.Right -> user.right()
            }
        }.mapLeft { t ->
            t.printStackTrace()
            analytics.recordException(t)
            UnknownFailure(t.message)
        }
    }

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

    override suspend fun fetchCurrentUser(): Either<FetchUserFailure, User> {
        val userId = auth.currentUser?.uid ?: return FetchUserFailure.UserIsNull.left()
        return fetchUser(userId)
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
}