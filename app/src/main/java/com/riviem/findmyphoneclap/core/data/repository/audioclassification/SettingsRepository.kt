package com.riviem.findmyphoneclap.core.data.repository.audioclassification

interface SettingsRepository {
    suspend fun setSensitivity(sensitivity: Int)
    suspend fun getSensitivity(): Int
    suspend fun setServiceActivated(isActivated: Boolean)
    suspend fun getServiceActivated(): Boolean
}