package com.riviem.findmyphoneclap.core.data.service.clapdetecting

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.riviem.findmyphoneclap.core.data.repository.audioclassification.SettingsRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeout
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
        withTimeout(5000) {
            while (!bound) {
                delay(100)
            }
        }
        if(bound) {
            onServiceBound()
        }
    }

    private suspend fun onServiceBound() {
        audioTFLite.serviceSettings = audioTFLite.serviceSettings.copy(
            sensitivity = settingsRepository.getSensitivity(),
            volume = settingsRepository.getVolume(),
            songDuration = settingsRepository.getSongDuration(),
            isBypassDNDPermissionEnabled = settingsRepository.getBypassDoNotDisturbPermissionEnabled(),
            currentSoundId = settingsRepository.getCurrentSoundId(),
            labels = settingsRepository.getLabels()
        )
        setCurrentSoundId(settingsRepository.getCurrentSoundId())
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
        if (!bound) {
            return
        }
        audioTFLite.serviceSettings = audioTFLite.serviceSettings.copy(volume = volume)
    }

    override fun setSongDuration(songDurationMillis: Long) {
        if (!bound) {
            return
        }
        audioTFLite.serviceSettings = audioTFLite.serviceSettings.copy(songDuration = songDurationMillis)
    }

    override fun setBypassDNDPermissionEnabled(isEnabled: Boolean) {
        if (!bound) {
            return
        }
        audioTFLite.serviceSettings = audioTFLite.serviceSettings.copy(isBypassDNDPermissionEnabled = isEnabled)
    }

    override suspend fun pauseServiceForDuration(durationMillis: Long) {
        if (!bound) {
            return
        }
        audioTFLite.pauseServiceForDuration(durationMillis)
    }

    override fun setCurrentSoundId(soundId: Int) {
        if (!bound) {
            return
        }
        audioTFLite.serviceSettings = audioTFLite.serviceSettings.copy(currentSoundId = soundId)
        audioTFLite.clearMediaPlayer()
        audioTFLite.createMediaPlayer(currentSound = soundId)
    }

    override fun setLabels(setOfLabels: Set<Label>) {
        if (!bound) {
            return
        }
        val newLabels = audioTFLite.serviceSettings.labels.toMutableSet()
        newLabels.addAll(setOfLabels)
        audioTFLite.serviceSettings = audioTFLite.serviceSettings.copy(labels = newLabels)
    }

    override fun clearLabels(setOfLabels: Set<Label>) {
        if (!bound) {
            return
        }
        val newLabels = audioTFLite.serviceSettings.labels.toMutableSet().minus(setOfLabels)
        audioTFLite.serviceSettings = audioTFLite.serviceSettings.copy(labels = newLabels)
    }
}