package com.lexwilliam.core.di

import android.content.Context
import com.lexwilliam.core.manager.ImageManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CoreModule {
    @Singleton
    @Provides
    fun provideImageManager(
        @ApplicationContext context: Context
    ): ImageManager {
        return ImageManager(context)
    }
}