package com.lexwilliam.company.di

import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.FirebaseFirestore
import com.lexwilliam.company.repository.CompanyRepository
import com.lexwilliam.company.repository.firebaseCompanyRepository
import com.lexwilliam.user.repository.UserRepository
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
    fun providesCompanyRepository(
        analytics: FirebaseCrashlytics,
        store: FirebaseFirestore,
        userRepository: UserRepository
    ): CompanyRepository = firebaseCompanyRepository(analytics, store, userRepository)
}