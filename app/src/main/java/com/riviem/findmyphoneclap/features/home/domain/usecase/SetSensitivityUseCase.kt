package com.riviem.findmyphoneclap.features.home.domain.usecase

import com.riviem.findmyphoneclap.core.data.repository.audioclassification.SettingsRepository
import com.riviem.findmyphoneclap.core.data.service.clapdetecting.AudioClassificationService
import javax.inject.Inject

class SetSensitivityUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val audioClassificationService: AudioClassificationService
) {
    suspend fun execute(newValue: Int) {
        audioClassificationService.setSensitivity(sensitivity = newValue)
        settingsRepository.setSensitivity(sensitivity = newValue)
    }
}