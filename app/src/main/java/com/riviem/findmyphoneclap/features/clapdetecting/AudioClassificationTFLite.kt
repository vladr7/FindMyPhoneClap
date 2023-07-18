package com.riviem.findmyphoneclap.features.clapdetecting

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.riviem.findmyphoneclap.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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
        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Audio Classification Service")
            .setContentText("Classifying audio...")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .build()
        startForeground(1, notification)
        CoroutineScope(Dispatchers.IO).launch { startRecording() }
        return START_STICKY
    }

    private suspend fun startRecording() {
        val audioRecord = audioClassifier.createAudioRecord()
        audioRecord.startRecording()

        val nrOfSecondsToListen = 1000000000
        var secondsCounter = 0
        while (secondsCounter < nrOfSecondsToListen) {
            delay(1000L)
            Log.d("AudioClassification", "Seconds passed: $secondsCounter")
            secondsCounter++
            tensorAudio.load(audioRecord)
            val listOfClassification: List<Classifications> = audioClassifier.classify(tensorAudio)
            for (classification in listOfClassification) {
                for (category in classification.categories) {
                    if (category.score > 0.3) {
                        Log.d("AudioClassification", "Category: ${category.label}, Score: ${category.score}")
                    }
                }
            }
        }
        audioRecord.stop()
        stopSelf()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
