package com.lexwilliam.product.di

import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.storage.FirebaseStorage
import com.lexwilliam.product.repository.ProductRepository
import com.lexwilliam.product.repository.TempProductRepository
import com.lexwilliam.product.repository.firebaseProductRepository
import com.lexwilliam.product.repository.inMemoryTempProductRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ProductModule {
    @Singleton
    @Provides
    fun providesProductRepository(
        analytics: FirebaseCrashlytics,
        store: FirebaseFirestore,
        storage: FirebaseStorage,
        functions: FirebaseFunctions,
        json: Json
    ): ProductRepository =
        firebaseProductRepository(
            analytics,
            store,
            storage,
            functions,
            json
        )

    @Singleton
    @Provides
    fun providesTempProductRepository(): TempProductRepository = inMemoryTempProductRepository()
}