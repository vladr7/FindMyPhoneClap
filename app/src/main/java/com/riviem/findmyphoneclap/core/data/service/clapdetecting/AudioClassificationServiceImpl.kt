package com.riviem.findmyphoneclap.core.data.service.clapdetecting

import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.provider.Settings
import javax.inject.Inject

class AudioClassificationServiceImpl @Inject constructor(
    private val context: Context,
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
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (!notificationManager.isNotificationPolicyAccessGranted) {
            val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
        val intent = Intent(context, AudioTFLite::class.java)
        context.startForegroundService(intent)
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    override fun stopService() {
        if (bound) {
            context.unbindService(connection)
            bound = false
        }
        audioTFLite.stopService()
    }
}