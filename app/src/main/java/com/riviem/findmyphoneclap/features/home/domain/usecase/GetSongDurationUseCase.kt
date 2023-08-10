package com.riviem.findmyphoneclap.features.home.domain.usecase

import com.riviem.findmyphoneclap.core.data.repository.audioclassification.SettingsRepository
import javax.inject.Inject

class GetSongDurationUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    suspend fun execute(): Int {
        val songDurationInSeconds = settingsRepository.getSongDuration() / 1000
        return songDurationInSeconds.toInt()
    }
}