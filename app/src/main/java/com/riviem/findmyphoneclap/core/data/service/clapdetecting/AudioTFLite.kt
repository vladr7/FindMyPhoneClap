package com.riviem.findmyphoneclap.core.data.service.clapdetecting

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.AudioRecord
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.riviem.findmyphoneclap.R
import com.riviem.findmyphoneclap.core.data.models.ServiceSettings
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.tensorflow.lite.support.audio.TensorAudio
import org.tensorflow.lite.support.label.Category
import org.tensorflow.lite.task.audio.classifier.AudioClassifier
import org.tensorflow.lite.task.audio.classifier.Classifications
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@AndroidEntryPoint
class AudioTFLite @Inject constructor() : Service() {
    private lateinit var audioClassifier: AudioClassifier
    private lateinit var tensorAudio: TensorAudio
    private lateinit var audioRecord: AudioRecord
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var audioManager: AudioManager
    var serviceSettings: ServiceSettings = ServiceSettings()
    private var coroutineScope = CoroutineScope(Dispatchers.IO)
    private val playSoundAfterCreatingMediaPlayerCoroutineScope = CoroutineScope(Dispatchers.IO)

    inner class LocalBinder : Binder() {
        fun getService(): AudioTFLite = this@AudioTFLite
    }

    private val binder = LocalBinder()

    companion object {
        const val CHANNEL_ID = "AudioClassificationChannel"
        var isServiceRunning = false
    }

    override fun onCreate() {
        super.onCreate()
        val modelName = "lite-model_yamnet_classification_tflite_1.tflite"
        try {
            audioClassifier = AudioClassifier.createFromFile(
                applicationContext,
                modelName
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        tensorAudio = audioClassifier.createInputTensorAudio()

        val serviceChannel = NotificationChannel(
            CHANNEL_ID,
            "Foreground Service Channel",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val manager = getSystemService(
            NotificationManager::class.java
        )
        manager.createNotificationChannel(serviceChannel)
    }

    @SuppressLint("ForegroundServiceType")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification: Notification = NotificationCompat.Builder(
            this,
            CHANNEL_ID
        )
            .setContentTitle("2ClapsAway")
            .setContentText("Clap detection is running")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .build()
        if (!isServiceRunning) {
            isServiceRunning = true
            startForeground(
                1,
                notification
            )
            coroutineScope.launch { startRecording() }
        }
        return START_STICKY
    }

    private suspend fun startRecording() {
        audioRecord = audioClassifier.createAudioRecord()
        audioRecord.startRecording()
        mediaPlayer = MediaPlayer.create(
            this,
            serviceSettings.currentSoundId
        )
        audioManager = this.getSystemService(Context.AUDIO_SERVICE) as AudioManager

        val nrOfSecondsToListen: Long = Long.MAX_VALUE
        var secondsCounter = 0L
        mediaPlayer.setOnErrorListener { mediaPlayer, i, i2 ->
            mediaPlayer.release()
            createMediaPlayer(currentSound = serviceSettings.currentSoundId)
            playSoundAfterCreatingMediaPlayerCoroutineScope.launch {
                playSound()
                this.cancel()
            }
            false
        }
        while (secondsCounter < nrOfSecondsToListen) {
            delay(1000L)
            if (secondsCounter % 300 == 0L) {
                restartRecording()
            }
            secondsCounter++
            tensorAudio.load(audioRecord)
            val listOfClassification: List<Classifications> = audioClassifier.classify(tensorAudio)
            for (classification in listOfClassification) {
                for (category in classification.categories) {
                    if (shouldPlaySound(category)) {
                        playSound()
                    }
                }
            }
        }
        mediaPlayer.setOnCompletionListener { mp ->
            mp.release()
        }
        audioRecord.stop()
        stopSelf()
    }

    private fun restartRecording() {
        audioRecord.stop()
        audioRecord.release()
        audioRecord = audioClassifier.createAudioRecord()
        audioRecord.startRecording()
    }

    private fun shouldPlaySound(category: Category): Boolean {
        val userScore = convertSensitivityToScore(serviceSettings.sensitivity)
        val isLabelEnabled = serviceSettings.labels.contains(Label.fromString(category.label))
        return isLabelEnabled &&
                category.score > userScore
    }

    private fun convertSensitivityToScore(sensitivity: Int): Double {
        return 1 - (sensitivity.toDouble() / 100)
    }

    fun clearMediaPlayer() {
        if(!::mediaPlayer.isInitialized) {
            return
        }
        if(mediaPlayer.isPlaying) {
            mediaPlayer.stop()
        }
        mediaPlayer.release()
    }

    fun createMediaPlayer(currentSound: Int) {
        mediaPlayer = MediaPlayer.create(
            this,
            currentSound
        )
    }

    private suspend fun playSound() {
        val originalVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        if (isDNDEnabled() && !serviceSettings.isBypassDNDPermissionEnabled) {
            return
        }
        val originalRingerMode = audioManager.ringerMode
        if (!mediaPlayer.isPlaying) {
            try {
                audioManager.setStreamVolume(
                    AudioManager.STREAM_MUSIC,
                    convertVolumeToStreamVolume(serviceSettings.volume),
                    0
                )
                audioManager.ringerMode = AudioManager.RINGER_MODE_NORMAL
                mediaPlayer.isLooping = true
                mediaPlayer.start()

                delay(serviceSettings.songDuration)

                mediaPlayer.stop()
                mediaPlayer.prepare()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                audioManager.setStreamVolume(
                    AudioManager.STREAM_MUSIC,
                    originalVolume,
                    0
                )
                audioManager.ringerMode = originalRingerMode
            }
        }
    }

    private fun isDNDEnabled(): Boolean {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (!notificationManager.isNotificationPolicyAccessGranted) {
            // todo maybe there's a way to check that the sound was not played because of DND and we can inform the user
            // todo also it seems that after i grant DND permission the app doesn't play the sound only after few seconds
            return false
        }

        val filter = notificationManager.currentInterruptionFilter
        return filter == NotificationManager.INTERRUPTION_FILTER_ALARMS ||
                filter == NotificationManager.INTERRUPTION_FILTER_NONE ||
                filter == NotificationManager.INTERRUPTION_FILTER_PRIORITY
    }


    private fun convertVolumeToStreamVolume(volume: Int): Int {
        return (volume * 0.15).toInt()
    }

    fun stopService() {
        coroutineScope.cancel("Service stopped")
        isServiceRunning = false
        stopSelf()
        try {
            if (this::audioClassifier.isInitialized && !audioClassifier.isClosed) {
                audioClassifier.close()
            }
            if (this::audioRecord.isInitialized) {
                audioRecord.release()
            }
            if (this::mediaPlayer.isInitialized) {
                mediaPlayer.release()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    fun pauseServiceForDuration(duration: Long) {
        coroutineScope.cancel("Service paused")
        coroutineScope = CoroutineScope(Dispatchers.Main)
        coroutineScope.launch {
            delay(duration)
            startRecording()
        }
    }

}
