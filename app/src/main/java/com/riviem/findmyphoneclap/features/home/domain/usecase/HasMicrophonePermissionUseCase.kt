package com.riviem.findmyphoneclap.features.home.domain.usecase

import com.riviem.findmyphoneclap.core.data.repository.audioclassification.SettingsRepository
import javax.inject.Inject

class HasMicrophonePermissionUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    fun execute(): Boolean {
        return settingsRepository.hasMicrophonePermission()
    }
}