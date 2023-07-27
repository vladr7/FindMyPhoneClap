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
import android.util.Log
import androidx.core.app.NotificationCompat
import com.riviem.findmyphoneclap.R
import com.riviem.findmyphoneclap.core.data.repository.audioclassification.SettingsRepository
import com.riviem.findmyphoneclap.features.home.data.models.BypassDNDState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.tensorflow.lite.support.audio.TensorAudio
import org.tensorflow.lite.support.label.Category
import org.tensorflow.lite.task.audio.classifier.AudioClassifier
import org.tensorflow.lite.task.audio.classifier.Classifications
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
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
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    @Inject
    lateinit var settingsRepository: SettingsRepository

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
            .setContentTitle("Audio Classification Service")
            .setContentText("Classifying audio...")
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
            R.raw.birdwhistle
        )
        val songDuration = mediaPlayer.duration
        audioManager = this.getSystemService(Context.AUDIO_SERVICE) as AudioManager

        val nrOfSecondsToListen: Long = Long.MAX_VALUE
        var secondsCounter = 0L
        mediaPlayer.setOnErrorListener { mediaPlayer, i, i2 ->
            settingsRepository.logToFile("Checking..: MediaPlayer: Error: $i, $i2 ---------------------------------")
            false
        }
        while (secondsCounter < nrOfSecondsToListen) {
            delay(1000L)
            settingsRepository.logToFile("Seconds passed: $secondsCounter")
            settingsRepository.logToFile("Checking..: AudioRecord: ${audioRecord.recordingState}")
            settingsRepository.logToFile("Checking..: IsServiceRunning: ${isServiceRunning}")
            settingsRepository.logToFile("Checking..: MediaPlayer: ${mediaPlayer}")
            Log.d(
                "AudioClassification",
                "Seconds passed: $secondsCounter"
            )
            secondsCounter++
            tensorAudio.load(audioRecord)
            val listOfClassification: List<Classifications> = audioClassifier.classify(tensorAudio)
            for (classification in listOfClassification) {
                for (category in classification.categories) {
                    if (category.score > 0.1) {
                        settingsRepository.logToFile(
                            "Category: ${category.label}, Score: ${category.score}"
                        )
                        Log.d(
                            "AudioClassification",
                            "Category: ${category.label}, Score: ${category.score}"
                        )
                    }
                    if (shouldPlaySound(category)) {
                        settingsRepository.logToFile("Playing sound")
                        playSound(songDuration)
                    }
                }
            }
        }
        mediaPlayer.setOnCompletionListener { mp ->
            settingsRepository.logToFile("Set On Completion Listener -> Media Player -> Releasing: ${audioRecord.recordingState}")
            mp.release()
        }
        settingsRepository.logToFile("STOPPING AUDIO RECORD!!! AND SERVICE !!!!: ${audioRecord.recordingState}")
        audioRecord.stop()
        stopSelf()
    }

    private suspend fun shouldPlaySound(category: Category): Boolean {
        val userScore = convertSensitivityToScore(settingsRepository.getSensitivity())
        return category.label == Labels.CLAPPING.stringValue &&
                category.score > userScore
    }

    private fun convertSensitivityToScore(sensitivity: Int): Double {
        return 1 - (sensitivity.toDouble() / 100)
    }

    private suspend fun playSound(songDuration: Int) {
        val originalVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        if (!shouldBypassDNDPermission(originalVolume)) {
            return
        }
        val originalRingerMode = audioManager.ringerMode
        if (!mediaPlayer.isPlaying) {
            try {
                audioManager.setStreamVolume(
                    AudioManager.STREAM_MUSIC,
                    convertVolumeToStreamVolume(settingsRepository.getVolume()),
                    0
                )
                audioManager.ringerMode = AudioManager.RINGER_MODE_NORMAL
                mediaPlayer.start()

            } finally {
                coroutineScope {
                    launch {
                        delay(songDuration.toLong())
                        audioManager.setStreamVolume(
                            AudioManager.STREAM_MUSIC,
                            originalVolume,
                            0
                        )
                        audioManager.ringerMode = originalRingerMode
                    }
                }
            }
        }
    }

    private suspend fun shouldBypassDNDPermission(originalVolume: Int): Boolean {
        return when (settingsRepository.hasBypassDoNotDisturbPermission()) {
            BypassDNDState.ENABLED -> {
                true
            }

            else -> {
                originalVolume != 0
            }
        }
    }

    private fun convertVolumeToStreamVolume(volume: Int): Int {
        return (volume * 0.15).toInt()
    }

    fun stopService() {
        settingsRepository.logToFile("STOPPING SERVICE!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
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
}
