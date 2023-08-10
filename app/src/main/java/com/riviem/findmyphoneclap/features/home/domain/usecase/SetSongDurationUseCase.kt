package com.riviem.findmyphoneclap.features.home.domain.usecase

import com.riviem.findmyphoneclap.core.data.repository.audioclassification.SettingsRepository
import javax.inject.Inject

class SetSongDurationUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    suspend fun execute(songDurationInSeconds: Int) {
        val songDurationInMillis = songDurationInSeconds.toLong() * 1000
        return settingsRepository.setSongDuration(duration = songDurationInMillis)
    }
}