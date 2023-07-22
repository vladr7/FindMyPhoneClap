package com.riviem.findmyphoneclap

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.riviem.findmyphoneclap.core.data.service.clapdetecting.AudioTFLite
import com.riviem.findmyphoneclap.navigation.MainNavigation
import com.riviem.findmyphoneclap.ui.theme.FindMyPhoneClapTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
}

