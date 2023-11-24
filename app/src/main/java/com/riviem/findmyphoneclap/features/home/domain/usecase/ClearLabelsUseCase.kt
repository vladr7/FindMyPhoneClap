package com.riviem.findmyphoneclap.features.home.domain.usecase

import com.riviem.findmyphoneclap.core.data.repository.audioclassification.SettingsRepository
import com.riviem.findmyphoneclap.core.data.service.clapdetecting.AudioClassificationService
import com.riviem.findmyphoneclap.core.data.service.clapdetecting.Label
import javax.inject.Inject

class ClearLabelsUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val audioClassificationService: AudioClassificationService
) {
    suspend fun execute(labels: Set<Label>) {
        settingsRepository.clearLabels(setOfLabels = labels)
        audioClassificationService.clearLabels(setOfLabels = labels)
    }
}