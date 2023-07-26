package com.riviem.findmyphoneclap.features.home.domain.usecase

import com.riviem.findmyphoneclap.core.data.repository.audioclassification.SettingsRepository
import javax.inject.Inject

class SetSensitivityUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    suspend fun execute(newValue: Int) {
        return settingsRepository.setSensitivity(sensitivity = newValue)
    }
}