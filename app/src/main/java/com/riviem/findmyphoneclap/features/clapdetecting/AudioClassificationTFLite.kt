package com.riviem.findmyphoneclap.features.clapdetecting

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.riviem.findmyphoneclap.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.tensorflow.lite.support.audio.TensorAudio
import org.tensorflow.lite.task.audio.classifier.AudioClassifier
import org.tensorflow.lite.task.audio.classifier.Classifications

class AudioClassificationTFLite : Service() {
    private lateinit var audioClassifier: AudioClassifier
    private lateinit var tensorAudio: TensorAudio

    companion object {
        const val CHANNEL_ID = "AudioClassificationChannel"
        var isServiceRunning = false
    }

    override fun onCreate() {
        super.onCreate()
        println("vlad: onCreate")
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
            CoroutineScope(Dispatchers.IO).launch { startRecording() }
        }
        return START_STICKY
    }

    private suspend fun startRecording() {
        val audioRecord = audioClassifier.createAudioRecord()
        audioRecord.startRecording()
        val mediaPlayer: MediaPlayer = MediaPlayer.create(
            this,
            R.raw.birdwhistle
        )
        val songDuration = mediaPlayer.duration

        val audioManager = this.getSystemService(Context.AUDIO_SERVICE) as AudioManager

        val nrOfSecondsToListen: Long = Long.MAX_VALUE
        var secondsCounter = 0L
        while (secondsCounter < nrOfSecondsToListen) {
            // fac aici 2 coroutine, unu face load la tensor la secunda 5000, altul la jumatatea secundei (5500), dar tot delay de o secunda)???
            delay(1000L)
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
                        Log.d(
                            "AudioClassification",
                            "Category: ${category.label}, Score: ${category.score}"
                        )
                    }
                    if (category.label == Labels.CLAPPING.stringValue &&
                        category.score > 0.5
                    ) {
                        // Salvează starea curentă a volumului și modului
                        val originalVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
                        val originalRingerMode = audioManager.ringerMode

                        if (!mediaPlayer.isPlaying) {
                            try {
                                // Setează volumul la maxim și modul la normal
                                audioManager.setStreamVolume(
                                    AudioManager.STREAM_MUSIC,
                                    audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
                                    0
                                )
                                audioManager.ringerMode = AudioManager.RINGER_MODE_NORMAL

                                // Redă sunetul
                                mediaPlayer.start()

                            } finally {
                                coroutineScope {
                                    launch {
                                        delay(songDuration.toLong())
                                        // Restaurează starea originală a volumului și modului
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
                }
            }
        }
        mediaPlayer.setOnCompletionListener { mp ->
            mp.release()
        }
        audioRecord.stop()
        stopSelf()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
