package com.riviem.findmyphoneclap.core.data.repository.audioclassification

import com.riviem.findmyphoneclap.core.data.datasource.local.LocalStorage
import com.riviem.findmyphoneclap.core.data.datasource.local.LocalStorageKeys
import javax.inject.Inject

class AudioClassificationRepositoryImpl @Inject constructor(
    private val localStorage: LocalStorage
): AudioClassificationRepository {

    override suspend fun setSensitivity(sensitivity: Int) {
        localStorage.putInt(LocalStorageKeys.SENSITIVITY, sensitivity)
    }

    override suspend fun getSensitivity(): Int {
        return localStorage.getInt(LocalStorageKeys.SENSITIVITY, 60)
    }

}