package com.riviem.findmyphoneclap.features.clapdetecting

import android.content.Context
import android.media.AudioRecord
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.tensorflow.lite.support.audio.TensorAudio
import org.tensorflow.lite.support.label.Category
import org.tensorflow.lite.task.audio.classifier.AudioClassifier
import org.tensorflow.lite.task.audio.classifier.Classifications

class AudioClassificationTFLite(
    private val context: Context
) : AudioClassification {

    private val modelName = "lite-model_yamnet_classification_tflite_1.tflite"
    private lateinit var audioRecord: AudioRecord

    private lateinit var audioClassifier: AudioClassifier
    private var tensorAudio: TensorAudio

    init {
        try {
            audioClassifier = AudioClassifier.createFromFile(
                context,
                modelName
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        tensorAudio = audioClassifier.createInputTensorAudio()
    }

    override fun startRecording() {
        val format = audioClassifier.requiredTensorAudioFormat
        val specs = "Number of channels ${format.channels}, \n" +
                "Sample rate ${format.sampleRate}, \n" +
                "Number of samples for input ${format.sampleRate}"
        println(specs)
        audioRecord = audioClassifier.createAudioRecord()
        audioRecord.startRecording()

        val nrOfSecondsToListen = 20
        var secondsCounter = 0
        val coroutineScope = CoroutineScope(Dispatchers.Main)
        coroutineScope.launch {
            val finalOutput: MutableList<Category> = mutableListOf()
            while (secondsCounter < nrOfSecondsToListen) {
                delay(1000L)
                println("Seconds passed: $secondsCounter")
                secondsCounter++
                tensorAudio.load(audioRecord)
                val listOfClassification: List<Classifications> = audioClassifier.classify(tensorAudio)
                for (classification in listOfClassification) {
                    for (category in classification.categories) {
                        if (category.score > 0.3) {
                            finalOutput.add(category)
                        }
                    }
                }
            }
            finalOutput.forEach {
                println("Category: ${it.label}, Score: ${it.score}")
            }
            audioRecord.stop()
        }
    }
}