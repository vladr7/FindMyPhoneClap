package com.riviem.findmyphoneclap.core.data.service.clapdetecting

interface AudioClassificationService {

    suspend fun startService()
    fun stopService()
    fun setSensitivity(sensitivity: Int)
}