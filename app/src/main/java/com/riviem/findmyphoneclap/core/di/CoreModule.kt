package com.riviem.findmyphoneclap.core.di

import android.app.Service
import android.content.Context
import com.riviem.findmyphoneclap.core.data.datasource.local.DefaultDataStore
import com.riviem.findmyphoneclap.core.data.datasource.local.LocalStorage
import com.riviem.findmyphoneclap.core.data.repository.audioclassification.AudioClassificationRepository
import com.riviem.findmyphoneclap.core.data.repository.audioclassification.AudioClassificationRepositoryImpl
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
    fun provideAudioTFLite(
    ): Service = AudioTFLite()

    @Singleton
    @Provides
    fun provideAudioClassificationService(
        @ApplicationContext context: Context,
        audioTFLite: AudioTFLite
    ): AudioClassificationService = AudioClassificationServiceImpl(
        context = context,
        audioTFLite = audioTFLite
    )

    @Singleton
    @Provides
    fun provideAudioClassificationRepository(
        localStorage: LocalStorage,
        audioClassificationService: AudioClassificationService
    ): AudioClassificationRepository = AudioClassificationRepositoryImpl(
        localStorage = localStorage,
        audioClassificationService = audioClassificationService
    )
}