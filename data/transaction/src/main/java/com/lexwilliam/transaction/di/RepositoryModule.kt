package com.lexwilliam.transaction.di

import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import com.lexwilliam.core.session.ObserveSessionUseCase
import com.lexwilliam.transaction.repository.TransactionRepository
import com.lexwilliam.transaction.repository.firebaseTransactionRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Singleton
    @Provides
    fun provideTransactionRepository(
        observeSession: ObserveSessionUseCase,
        store: FirebaseFirestore,
        analytics: FirebaseCrashlytics,
        functions: FirebaseFunctions,
        json: Json
    ): TransactionRepository =
        firebaseTransactionRepository(
            observeSession,
            store,
            analytics,
            functions,
            json
        )
}