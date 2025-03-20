package com.lexwilliam.user.session

import android.util.Log
import arrow.core.Either
import arrow.core.right
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.lexwilliam.firebase.model.AuthClaims
import com.lexwilliam.user.source.SessionManager
import com.lexwilliam.user.util.LoginFailure
import com.lexwilliam.user.util.LogoutFailure
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
                            val branchUUID = result.claims[AuthClaims.BRANCH_UUID.path]?.toString()
                            val role = result.claims[AuthClaims.ROLE.path]?.toString()
                            val userUUID = firebaseAuth.currentUser?.uid
                            Log.d("SESSION", "$userUUID")
                            Log.d("SESSION", branchUUID.toString())
                            Log.d("SESSION", role.toString())
                            val session =
                                Session(userUUID = userUUID, branchUUID = branchUUID, role = role)
                            trySend(session)
                        }
                        ?.addOnFailureListener {
                                exception ->
                            crashlytics.recordException(exception)
                        }
                        ?: trySend(Session())
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