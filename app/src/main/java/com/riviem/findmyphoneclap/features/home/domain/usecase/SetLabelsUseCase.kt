package com.riviem.findmyphoneclap.features.home.domain.usecase

import com.riviem.findmyphoneclap.core.data.repository.audioclassification.SettingsRepository
import com.riviem.findmyphoneclap.core.data.service.clapdetecting.AudioClassificationService
import com.riviem.findmyphoneclap.core.data.service.clapdetecting.Label
import javax.inject.Inject

class SetLabelsUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val audioClassificationService: AudioClassificationService
) {
    suspend fun execute(setOfLabels: Set<Label>) {
        audioClassificationService.setLabels(setOfLabels = setOfLabels)
        settingsRepository.setLabels(setOfLabels = setOfLabels)
    }
}