package com.riviem.findmyphoneclap.core.data.repository.audioclassification

interface SettingsRepository {
    suspend fun setSensitivity(sensitivity: Int)
    suspend fun getSensitivity(): Int
    suspend fun setVolume(volume: Int)
    suspend fun getVolume(): Int
    fun hasMicrophonePermission(): Boolean
    fun hasBypassDoNotDisturbPermission(): Boolean
    fun askForBypassDoNotDisturbPermission()
}