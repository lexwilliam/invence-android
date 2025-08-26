package com.lexwilliam.firebase.session

import arrow.core.Either
import arrow.core.right
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.lexwilliam.core.session.Session
import com.lexwilliam.core.session.SessionFailure
import com.lexwilliam.core.session.SessionManager
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

fun firebaseSessionManager(
    auth: FirebaseAuth,
    crashlytics: FirebaseCrashlytics
) = object : SessionManager {
    override val sessionDataFlow: Flow<Session> =
        callbackFlow {
            val listener =
                FirebaseAuth.IdTokenListener { firebaseAuth ->
                    firebaseAuth
                        .currentUser
                        ?.getIdToken(false)
                        ?.addOnSuccessListener { result ->
                            val userUUID = firebaseAuth.currentUser?.uid
                            val session =
                                Session.Authenticated(userUUID = userUUID)
                            trySend(session)
                        }
                        ?.addOnFailureListener {
                                exception ->
                            crashlytics.recordException(exception)
                        }
                        ?: trySend(Session.Unauthenticated)
                }
            auth.addIdTokenListener(listener)

            awaitClose { auth.removeIdTokenListener(listener) }
        }

    override suspend fun saveSession(uuid: String): Either<SessionFailure, Boolean> = true.right()

    override suspend fun clearSession(): Either<SessionFailure, Boolean> {
        auth.signOut()
        return true.right()
    }
}