package com.lexwilliam.product.di

import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.FirebaseFirestore
import com.lexwilliam.product.repository.ProductRepository
import com.lexwilliam.product.repository.firebaseProductRepository
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
    fun providesProductRepository(
        analytics: FirebaseCrashlytics,
        store: FirebaseFirestore
    ): ProductRepository = firebaseProductRepository(analytics, store)
}