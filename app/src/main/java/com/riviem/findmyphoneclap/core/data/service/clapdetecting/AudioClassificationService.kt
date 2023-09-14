package com.riviem.findmyphoneclap.core.data.service.clapdetecting

interface AudioClassificationService {

    suspend fun startService()
    fun stopService()
    fun isServiceRunning(): Boolean
    fun setSensitivity(sensitivity: Int)
    fun setVolume(volume: Int)
    fun setSongDuration(songDurationMillis: Long)
    fun setBypassDNDPermissionEnabled(isEnabled: Boolean)
    suspend fun pauseServiceForDuration(durationMillis: Long)
    fun setCurrentSoundId(soundId: Int)
}