package com.lexwilliam.product.di

import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import com.lexwilliam.firebase.utils.StorageUploader
import com.lexwilliam.product.repository.ProductRepository
import com.lexwilliam.product.repository.firebaseProductRepository
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
        storageUploader: StorageUploader,
        functions: FirebaseFunctions,
        json: Json
    ): ProductRepository =
        firebaseProductRepository(
            analytics,
            store,
            storageUploader,
            functions,
            json
        )
}