package com.riviem.findmyphoneclap.core.data.repository.audioclassification

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.riviem.findmyphoneclap.core.constants.Constants
import com.riviem.findmyphoneclap.core.data.datasource.local.LocalStorage
import com.riviem.findmyphoneclap.core.data.datasource.local.LocalStorageKeys
import javax.inject.Inject


class SettingsRepositoryImpl @Inject constructor(
    private val context: Context,
    private val localStorage: LocalStorage,
) : SettingsRepository {

    companion object {
        const val MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 1
    }

    override suspend fun setSensitivity(sensitivity: Int) {
        localStorage.putInt(
            LocalStorageKeys.SENSITIVITY,
            sensitivity
        )
    }

    override suspend fun getSensitivity(): Int {
        return localStorage.getInt(
            LocalStorageKeys.SENSITIVITY,
            Constants.SENSITIVITY_DEFAULT
        )
    }

    override suspend fun setVolume(volume: Int) {
        localStorage.putInt(
            LocalStorageKeys.VOLUME,
            volume
        )
    }

    override suspend fun getVolume(): Int {
        return localStorage.getInt(
            LocalStorageKeys.VOLUME,
            Constants.VOLUME_DEFAULT
        )
    }

    override fun hasMicrophonePermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun askForMicrophonePermission(activity: Activity) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    Manifest.permission.RECORD_AUDIO
                )
            ) {
                AlertDialog.Builder(activity)
                    .setTitle("Permisiune necesară")
                    .setMessage("Această aplicație are nevoie de permisiunea de a înregistra audio pentru a funcționa corect. Te rugăm să o acorzi din setările aplicației.")
                    .setPositiveButton("Setări") { _, _ ->
                        val intent = Intent()
                        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        val uri = Uri.fromParts(
                            "package",
                            activity.packageName,
                            null
                        )
                        intent.data = uri
                        activity.startActivity(intent)
                    }
                    .create()
                    .show()
            } else {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(Manifest.permission.RECORD_AUDIO),
                    MY_PERMISSIONS_REQUEST_RECORD_AUDIO
                )
            }
        }
    }


    override fun hasBypassDoNotDisturbPermission(): Boolean {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        return notificationManager.isNotificationPolicyAccessGranted
    }

    override fun askForBypassDoNotDisturbPermission() {
        val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }
}