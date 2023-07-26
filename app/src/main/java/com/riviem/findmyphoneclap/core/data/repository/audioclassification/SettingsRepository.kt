package com.riviem.findmyphoneclap.core.data.repository.audioclassification

import com.riviem.findmyphoneclap.features.home.data.models.BypassDNDState

interface SettingsRepository {
    suspend fun setSensitivity(sensitivity: Int)
    suspend fun getSensitivity(): Int
    suspend fun setVolume(volume: Int)
    suspend fun getVolume(): Int
    fun hasMicrophonePermission(): Boolean
    suspend fun hasBypassDoNotDisturbPermission(): BypassDNDState
    fun askForBypassDoNotDisturbPermission()
    suspend fun setByPassDoNotDisturbPermission(isEnabled: Boolean)
}