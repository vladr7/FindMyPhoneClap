package com.riviem.findmyphoneclap.features.home.domain.usecase

import com.riviem.findmyphoneclap.core.data.repository.audioclassification.SettingsRepository
import com.riviem.findmyphoneclap.features.home.data.models.BypassDNDState
import javax.inject.Inject

class HasBypassDNDPermissionUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    suspend fun execute(): BypassDNDState {
        return settingsRepository.getBypassDoNotDisturbPermissionState()
    }
}