package com.riviem.findmyphoneclap.features.home.domain.usecase

import com.riviem.findmyphoneclap.core.data.repository.audioclassification.SettingsRepository
import com.riviem.findmyphoneclap.core.data.service.clapdetecting.AudioClassificationService
import javax.inject.Inject

class SetVolumeUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val audioClassificationService: AudioClassificationService
) {
    suspend fun execute(newValue: Int) {
        settingsRepository.setVolume(volume = newValue)
        audioClassificationService.setVolume(volume = newValue)
    }
}