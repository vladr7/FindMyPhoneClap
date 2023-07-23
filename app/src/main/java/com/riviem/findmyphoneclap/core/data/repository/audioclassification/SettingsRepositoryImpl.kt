package com.riviem.findmyphoneclap.core.data.repository.audioclassification

import com.riviem.findmyphoneclap.core.constants.Constants
import com.riviem.findmyphoneclap.core.data.datasource.local.LocalStorage
import com.riviem.findmyphoneclap.core.data.datasource.local.LocalStorageKeys
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val localStorage: LocalStorage,
): SettingsRepository {

    override suspend fun setSensitivity(sensitivity: Int) {
        localStorage.putInt(LocalStorageKeys.SENSITIVITY, sensitivity)
    }

    override suspend fun getSensitivity(): Int {
        return localStorage.getInt(LocalStorageKeys.SENSITIVITY, Constants.SENSITIVITY_DEFAULT)
    }

    override suspend fun setVolume(volume: Int) {
        localStorage.putInt(LocalStorageKeys.VOLUME, volume)
    }

    override suspend fun getVolume(): Int {
        return localStorage.getInt(LocalStorageKeys.VOLUME, Constants.VOLUME_DEFAULT)
    }

    override suspend fun setServiceActivated(isActivated: Boolean) {
        localStorage.putBoolean(LocalStorageKeys.SERVICE_ACTIVATED, isActivated)
    }

    override suspend fun getServiceActivated(): Boolean {
        return localStorage.getBoolean(LocalStorageKeys.SERVICE_ACTIVATED, false)
    }


}