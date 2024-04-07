package com.lexwilliam.log.di

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.FirebaseFirestore
import com.lexwilliam.log.repository.LogRepository
import com.lexwilliam.log.repository.firebaseLogRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LogModule {
    @RequiresApi(Build.VERSION_CODES.O)
    @Singleton
    @Provides
    fun providesLogRepository(
        analytics: FirebaseCrashlytics,
        store: FirebaseFirestore
    ): LogRepository = firebaseLogRepository(analytics, store)
}