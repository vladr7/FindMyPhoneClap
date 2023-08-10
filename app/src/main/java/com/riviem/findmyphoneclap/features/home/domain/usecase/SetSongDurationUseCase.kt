package com.riviem.findmyphoneclap.features.home.domain.usecase

import com.riviem.findmyphoneclap.core.data.repository.audioclassification.SettingsRepository
import com.riviem.findmyphoneclap.core.data.service.clapdetecting.AudioClassificationService
import javax.inject.Inject

class SetSongDurationUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val audioClassificationService: AudioClassificationService
) {
    suspend fun execute(songDurationInSeconds: Int) {
        val songDurationInMillis = songDurationInSeconds.toLong() * 1000
        settingsRepository.setSongDuration(durationInMillis = songDurationInMillis)
        audioClassificationService.setSongDuration(songDurationMillis = songDurationInMillis)
    }
}