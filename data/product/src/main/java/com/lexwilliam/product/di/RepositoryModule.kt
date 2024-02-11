package com.lexwilliam.product.di

import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.lexwilliam.product.repository.ProductRepository
import com.lexwilliam.product.repository.TempProductRepository
import com.lexwilliam.product.repository.firebaseProductRepository
import com.lexwilliam.product.repository.inMemoryTempProductRepository
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
        store: FirebaseFirestore,
        storage: FirebaseStorage
    ): ProductRepository = firebaseProductRepository(analytics, store, storage)

    @Singleton
    @Provides
    fun providesTempProductRepository(): TempProductRepository = inMemoryTempProductRepository()
}