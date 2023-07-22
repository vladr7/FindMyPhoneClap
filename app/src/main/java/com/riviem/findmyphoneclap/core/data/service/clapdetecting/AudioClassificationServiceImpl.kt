package com.riviem.findmyphoneclap.core.data.service.clapdetecting

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.provider.Settings
import javax.inject.Inject

class AudioClassificationServiceImpl @Inject constructor(
    private val context: Context,
    private val audioTFLite: AudioTFLite
): AudioClassificationService {

    override suspend fun startService() {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (!notificationManager.isNotificationPolicyAccessGranted) {
            val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
        val intent = Intent(context, AudioTFLite::class.java)
        context.startForegroundService(intent)
    }

    override fun stopService() {
        audioTFLite.stopService()
    }
}