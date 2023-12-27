package com.lexwilliam.user.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.lexwilliam.user.session.firebaseSessionManager
import com.lexwilliam.user.source.SessionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UserModule {
    @Singleton
    @Provides
    fun providesSessionManager(
        auth: FirebaseAuth,
        crashlytics: FirebaseCrashlytics,
    ): SessionManager = firebaseSessionManager(auth, crashlytics)
}
