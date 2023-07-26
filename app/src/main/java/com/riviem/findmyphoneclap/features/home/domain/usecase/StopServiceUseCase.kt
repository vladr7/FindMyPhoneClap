package com.riviem.findmyphoneclap.features.home.domain.usecase

import com.riviem.findmyphoneclap.core.data.service.clapdetecting.AudioClassificationService
import javax.inject.Inject

class StopServiceUseCase @Inject constructor(
    private val audioClassificationService: AudioClassificationService
) {
    fun execute() {
        return audioClassificationService.stopService()
    }
}