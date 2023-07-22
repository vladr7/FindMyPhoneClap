package com.riviem.findmyphoneclap.core.data.repository.audioclassification

import com.riviem.findmyphoneclap.core.constants.Constants
import com.riviem.findmyphoneclap.core.data.datasource.local.LocalStorage
import com.riviem.findmyphoneclap.core.data.datasource.local.LocalStorageKeys
import com.riviem.findmyphoneclap.core.data.service.clapdetecting.AudioClassificationService
import javax.inject.Inject

class AudioClassificationRepositoryImpl @Inject constructor(
    private val localStorage: LocalStorage,
    private val audioClassificationService: AudioClassificationService
): AudioClassificationRepository {

    override suspend fun startService() {
        audioClassificationService.startService()
    }

    override fun stopService() {
        audioClassificationService.stopService()
    }

    override suspend fun setSensitivity(sensitivity: Int) {
        localStorage.putInt(LocalStorageKeys.SENSITIVITY, sensitivity)
        audioClassificationService.setSensitivity(sensitivity)
    }

    override suspend fun getSensitivity(): Int {
        return localStorage.getInt(LocalStorageKeys.SENSITIVITY, Constants.SENSITIVITY_DEFAULT)
    }

}