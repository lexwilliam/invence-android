package com.lexwilliam.branch.di

import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.FirebaseFirestore
import com.lexwilliam.branch.repository.BranchRepository
import com.lexwilliam.branch.repository.firebaseBranchRepository
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
    fun providesBranchRepository(
        analytics: FirebaseCrashlytics,
        store: FirebaseFirestore
    ): BranchRepository = firebaseBranchRepository(analytics, store)
}