package com.lexwilliam.transaction.di

import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.FirebaseFirestore
import com.lexwilliam.transaction.repository.TransactionRepository
import com.lexwilliam.transaction.repository.firebaseTransactionRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Singleton
    @Provides
    fun provideTransactionRepository(
        store: FirebaseFirestore,
        analytics: FirebaseCrashlytics
    ): TransactionRepository = firebaseTransactionRepository(store, analytics)
}