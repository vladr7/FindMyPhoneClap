package com.riviem.findmyphoneclap.features.home.domain.usecase

import com.riviem.findmyphoneclap.core.data.repository.audioclassification.SettingsRepository
import com.riviem.findmyphoneclap.core.data.service.clapdetecting.AudioClassificationService
import com.riviem.findmyphoneclap.core.data.service.clapdetecting.Label
import javax.inject.Inject

class GetLabelsUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
) {
    suspend fun execute(): Set<Label> {
        return settingsRepository.getLabels()
    }
}