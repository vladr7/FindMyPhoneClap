package com.riviem.findmyphoneclap

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.riviem.findmyphoneclap.navigation.MainNavigation
import com.riviem.findmyphoneclap.ui.theme.FindMyPhoneClapTheme
import dagger.hilt.android.AndroidEntryPoint
import android.Manifest
import com.riviem.findmyphoneclap.core.data.repository.audioclassification.SettingsRepositoryImpl.Companion.MY_PERMISSIONS_REQUEST_RECORD_AUDIO


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        askForMicrophonePermission()

        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            val viewModel: MainViewModel = hiltViewModel()
            val state = viewModel.state.collectAsStateWithLifecycle()
            FindMyPhoneClapTheme {
                window?.statusBarColor =
                    MaterialTheme.colorScheme.secondary.toArgb()
                window?.navigationBarColor =
                    MaterialTheme.colorScheme.secondary.toArgb()
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainNavigation()
                }
            }
        }
    }

//    private fun askForMicrophonePermission() {
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {
//                AlertDialog.Builder(this)
//                    .setTitle("Permisiune necesară")
//                    .setMessage("Această aplicație are nevoie de permisiunea de a înregistra audio pentru a funcționa corect.")
//                    .setPositiveButton("OK") { _, _ ->
//                        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), MY_PERMISSIONS_REQUEST_RECORD_AUDIO)
//                    }
//                    .create()
//                    .show()
//            } else {
//                // Dacă nu este nevoie de o explicație, cerem direct permisiunea
//                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), MY_PERMISSIONS_REQUEST_RECORD_AUDIO)
//            }
//        } else {
//            // Permisiunea a fost deja acordată, puteți continua cu funcționalitatea ce depinde de această permisiune
//        }
//
//    }
}

