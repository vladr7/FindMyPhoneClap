package com.riviem.findmyphoneclap.core.data.repository.audioclassification

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Settings
import androidx.core.content.ContextCompat
import com.riviem.findmyphoneclap.core.constants.Constants
import com.riviem.findmyphoneclap.core.data.datasource.local.LocalStorage
import com.riviem.findmyphoneclap.core.data.datasource.local.LocalStorageKeys
import com.riviem.findmyphoneclap.features.home.data.models.BypassDNDState
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

    override fun askForBypassDoNotDisturbPermission() {
        val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    override suspend fun hasBypassDoNotDisturbPermission(): BypassDNDState {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (!notificationManager.isNotificationPolicyAccessGranted) {
            return BypassDNDState.DISABLED_FROM_SYSTEM
        }
        val localStorageValue =  localStorage.getBoolean(
            LocalStorageKeys.BYPASS_DO_NOT_DISTURB_PERMISSION_ENABLED,
            false
        )
        if(!localStorageValue) {
            return BypassDNDState.DISABLED_FROM_LOCAL_STORAGE
        }
        return BypassDNDState.ENABLED
    }

    override suspend fun setByPassDoNotDisturbPermission(isEnabled: Boolean) {
        localStorage.putBoolean(
            LocalStorageKeys.BYPASS_DO_NOT_DISTURB_PERMISSION_ENABLED,
            isEnabled
        )
    }
}