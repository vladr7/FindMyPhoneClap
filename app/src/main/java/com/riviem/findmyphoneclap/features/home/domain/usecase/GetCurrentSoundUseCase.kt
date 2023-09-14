package com.riviem.findmyphoneclap.features.home.domain.usecase

import com.riviem.findmyphoneclap.core.data.repository.audioclassification.SettingsRepository
import javax.inject.Inject

class GetCurrentSoundUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
) {
    suspend fun execute(): Int {
        return settingsRepository.getCurrentSoundId()
    }
}