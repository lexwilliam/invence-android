package com.lexwilliam.user.session

import arrow.core.Either
import arrow.core.right
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.lexwilliam.user.repository.UserRepository
import com.lexwilliam.user.source.SessionManager
import com.lexwilliam.user.util.LoginFailure
import com.lexwilliam.user.util.LogoutFailure
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

fun firebaseSessionManager(
    auth: FirebaseAuth,
    crashlytics: FirebaseCrashlytics,
    userRepository: UserRepository
) = object : SessionManager {
    override val sessionDataFlow: Flow<Session> =
        callbackFlow {
            val listener =
                FirebaseAuth.IdTokenListener { firebaseAuth ->
                    firebaseAuth
                        .currentUser
                        ?.getIdToken(false)
                        ?.addOnSuccessListener {
                            val userUUID = firebaseAuth.currentUser?.uid
//                            val userData =
//                                runBlocking { userUUID?.let { userRepository.fetchUser(it) } }
                            val session =
                                Session(
                                    userUUID = userUUID
//                                    branchUUID = userData?.getOrNull()?.branchUUID?.toString()
                                )
                            trySend(session)
                        }
                        ?.addOnFailureListener { exception ->
                            crashlytics.recordException(exception)
                        }
                    if (firebaseAuth.currentUser?.getIdToken(false) == null) {
                        trySend(Session())
                    }
                }

            auth.addIdTokenListener(listener)

            awaitClose { auth.removeIdTokenListener(listener) }
        }

    override suspend fun saveSession(
        uuid: String,
        branchUUID: String
    ): Either<LoginFailure, Boolean> = true.right()

    override suspend fun clearSession(): Either<LogoutFailure, Boolean> {
        auth.signOut()
        return true.right()
    }
}