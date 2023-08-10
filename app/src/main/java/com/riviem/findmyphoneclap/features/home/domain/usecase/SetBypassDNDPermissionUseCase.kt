package com.riviem.findmyphoneclap.features.home.domain.usecase

import com.riviem.findmyphoneclap.core.data.repository.audioclassification.SettingsRepository
import com.riviem.findmyphoneclap.core.data.service.clapdetecting.AudioClassificationService
import com.riviem.findmyphoneclap.features.home.data.models.BypassDNDState
import javax.inject.Inject

class SetBypassDNDPermissionUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val audioClassificationService: AudioClassificationService
) {
    suspend fun execute(isEnabled: Boolean) {
        settingsRepository.setByPassDoNotDisturbPermission(isEnabled)
        audioClassificationService.setBypassDNDPermissionEnabled(isEnabled)
    }
}