package com.riviem.findmyphoneclap.core.di

import android.app.Service
import android.content.Context
import com.riviem.findmyphoneclap.core.data.datasource.local.DefaultDataStore
import com.riviem.findmyphoneclap.core.data.datasource.local.LocalStorage
import com.riviem.findmyphoneclap.core.data.repository.audioclassification.SettingsRepository
import com.riviem.findmyphoneclap.core.data.repository.audioclassification.SettingsRepositoryImpl
import com.riviem.findmyphoneclap.core.data.service.clapdetecting.AudioClassificationService
import com.riviem.findmyphoneclap.core.data.service.clapdetecting.AudioClassificationServiceImpl
import com.riviem.findmyphoneclap.core.data.service.clapdetecting.AudioTFLite
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
    fun provideLocalStorage(
        @ApplicationContext context: Context
    ): LocalStorage = DefaultDataStore(
        context = context
    )

    @Singleton
    @Provides
    fun provideSettingsRepository(
        localStorage: LocalStorage
    ): SettingsRepository = SettingsRepositoryImpl(
        localStorage = localStorage
    )


    @Singleton
    @Provides
    fun provideAudioClassificationService(
        @ApplicationContext context: Context,
    ): AudioClassificationService = AudioClassificationServiceImpl(
        context = context,
    )
}