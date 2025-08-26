package com.lexwilliam.firebase.di

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.PersistentCacheSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.lexwilliam.core.session.SessionManager
import com.lexwilliam.firebase.session.firebaseSessionManager
import com.lexwilliam.firebase.utils.StorageUploader
import com.lexwilliam.firebase.utils.TimestampSerializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {
    @Singleton
    @Provides
    fun provideFirestore(): FirebaseFirestore {
        val client = Firebase.firestore
        val settings =
            FirebaseFirestoreSettings
                .Builder()
                .setLocalCacheSettings(PersistentCacheSettings.newBuilder().build())
                .build()
        client.firestoreSettings = settings
        return client
    }

    @Singleton
    @Provides
    fun provideFirebaseAuth(): FirebaseAuth = Firebase.auth

    @Singleton
    @Provides
    fun provideFirebaseCrashlytics(): FirebaseCrashlytics {
        return FirebaseCrashlytics.getInstance()
    }

    @Singleton
    @Provides
    fun provideFirebaseStorage(): FirebaseStorage {
        return FirebaseStorage.getInstance()
    }

    @Singleton
    @Provides
    fun provideFirebaseFunction(): FirebaseFunctions {
        return Firebase.functions("asia-southeast2")
    }

    @Singleton
    @Provides
    fun provideJson(): Json {
        return Json {
            serializersModule =
                SerializersModule {
                    contextual(TimestampSerializer)
                }
        }
    }

    @Singleton
    @Provides
    fun provideStorageUploader(
        analytics: FirebaseCrashlytics,
        storage: FirebaseStorage,
        @ApplicationContext context: Context
    ) = StorageUploader(analytics, storage, context)

    @Singleton
    @Provides
    fun providesSessionManager(
        auth: FirebaseAuth,
        crashlytics: FirebaseCrashlytics
    ): SessionManager = firebaseSessionManager(auth, crashlytics)
}