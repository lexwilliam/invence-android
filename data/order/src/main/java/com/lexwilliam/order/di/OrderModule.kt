package com.lexwilliam.order.di

import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.FirebaseFirestore
import com.lexwilliam.core.session.ObserveSessionUseCase
import com.lexwilliam.order.repository.OrderRepository
import com.lexwilliam.order.repository.firebaseOrderRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object OrderModule {
    @Singleton
    @Provides
    fun provideOrderRepository(
        observeSession: ObserveSessionUseCase,
        analytics: FirebaseCrashlytics,
        store: FirebaseFirestore
    ): OrderRepository = firebaseOrderRepository(observeSession, analytics, store)
}