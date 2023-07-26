package com.riviem.findmyphoneclap.features.home.domain.usecase

import com.riviem.findmyphoneclap.core.data.service.clapdetecting.AudioClassificationService
import javax.inject.Inject

class StartServiceUseCase @Inject constructor(
    private val audioClassificationService: AudioClassificationService
) {
    suspend fun execute() {
        return audioClassificationService.startService()
    }
}