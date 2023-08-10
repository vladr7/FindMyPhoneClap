package com.riviem.findmyphoneclap.core.data.service.clapdetecting

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.riviem.findmyphoneclap.core.data.repository.audioclassification.SettingsRepository
import kotlinx.coroutines.delay
import javax.inject.Inject

class AudioClassificationServiceImpl @Inject constructor(
    private val context: Context,
    private val settingsRepository: SettingsRepository
): AudioClassificationService {

    private lateinit var audioTFLite: AudioTFLite
    private var bound = false

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            audioTFLite = (service as AudioTFLite.LocalBinder).getService()
            bound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            bound = false
        }
    }

    override suspend fun startService() {
        val intent = Intent(context, AudioTFLite::class.java)
        context.startForegroundService(intent)
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
        delay(1000) // todo remove this delay?
        audioTFLite.serviceSettings = audioTFLite.serviceSettings.copy(
            sensitivity = settingsRepository.getSensitivity(),
            volume = settingsRepository.getVolume(),
            songDuration = settingsRepository.getSongDuration(),
            isBypassDNDPermissionEnabled = settingsRepository.getBypassDoNotDisturbPermissionEnabled()
        )
    }

    override fun stopService() {
        if (bound) {
            context.unbindService(connection)
            bound = false
        }
        audioTFLite.stopService()
    }

    override fun isServiceRunning(): Boolean {
        return bound
    }

    override fun setSensitivity(sensitivity: Int) {
        audioTFLite.serviceSettings = audioTFLite.serviceSettings.copy(sensitivity = sensitivity)
    }

    override fun setVolume(volume: Int) {
        audioTFLite.serviceSettings = audioTFLite.serviceSettings.copy(volume = volume)
    }

    override fun setSongDuration(songDurationMillis: Long) {
        audioTFLite.serviceSettings = audioTFLite.serviceSettings.copy(songDuration = songDurationMillis)
    }

    override fun setBypassDNDPermissionEnabled(isEnabled: Boolean) {
        audioTFLite.serviceSettings = audioTFLite.serviceSettings.copy(isBypassDNDPermissionEnabled = isEnabled)
    }
}