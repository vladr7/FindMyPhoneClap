package com.riviem.findmyphoneclap.core.data.repository.audioclassification

interface AudioClassificationRepository {

    suspend fun startService()
    fun stopService()
    suspend fun setSensitivity(sensitivity: Int)
    suspend fun getSensitivity(): Int
}