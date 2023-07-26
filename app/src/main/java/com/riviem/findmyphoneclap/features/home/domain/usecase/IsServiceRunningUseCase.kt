package com.riviem.findmyphoneclap.features.home.domain.usecase

import com.riviem.findmyphoneclap.core.data.service.clapdetecting.AudioClassificationService
import javax.inject.Inject

class IsServiceRunningUseCase @Inject constructor(
    private val audioClassificationService: AudioClassificationService
) {
    fun execute(): Boolean {
        return audioClassificationService.isServiceRunning()
    }
}