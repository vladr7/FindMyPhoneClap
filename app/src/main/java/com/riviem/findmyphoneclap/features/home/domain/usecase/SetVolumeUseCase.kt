package com.riviem.findmyphoneclap.features.home.domain.usecase

import com.riviem.findmyphoneclap.core.data.repository.audioclassification.SettingsRepository
import javax.inject.Inject

class SetVolumeUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    suspend fun execute(newValue: Int) {
        return settingsRepository.setVolume(volume = newValue)
    }
}