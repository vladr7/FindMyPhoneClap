package com.riviem.findmyphoneclap.features.home.domain.usecase

import com.riviem.findmyphoneclap.core.data.service.clapdetecting.AudioClassificationService
import javax.inject.Inject

class PauseServiceForDurationUseCase @Inject constructor(
    private val audioClassificationService: AudioClassificationService
) {
    suspend fun execute(pauseDuration: Int) {
        val pauseDurationInMillis = pauseDuration.toLong() * 1000
        audioClassificationService.pauseServiceForDuration(durationMillis = pauseDurationInMillis)
    }
}