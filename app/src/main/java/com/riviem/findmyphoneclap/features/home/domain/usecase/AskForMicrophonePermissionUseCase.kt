package com.riviem.findmyphoneclap.features.home.domain.usecase

import android.app.Activity
import com.riviem.findmyphoneclap.core.data.repository.audioclassification.SettingsRepository
import javax.inject.Inject

class AskForMicrophonePermissionUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    fun execute(activity: Activity) {
        return settingsRepository.askForMicrophonePermission(activity = activity)
    }
}