package com.riviem.findmyphoneclap.features.settings.presentation

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.riviem.findmyphoneclap.MainActivity

@Composable
fun SettingsRoute(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val activity = context as MainActivity

    SettingsScreen(
        activity = activity,
        context = context,
        songDuration = state.songDuration,
        onSongDurationChange = { newValue ->
            viewModel.onSongDurationChange(newValue)
        },
        onBypassDoNotDisturbClick = {
            viewModel.onBypassDoNotDisturbClick()
        },
        isBypassDNDActive = state.isBypassDNDActive,
    )
}

@Composable
fun SettingsScreen(
    activity: Activity,
    context: Context,
    songDuration: Int,
    onSongDurationChange: (Int) -> Unit,
    onBypassDoNotDisturbClick: () -> Unit,
    isBypassDNDActive: Boolean,
) {
    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        RemoveUnusedAppPermissionButton(activity = activity)
        BypassDoNotDisturbButton(
            onActivationClick = {
                onBypassDoNotDisturbClick()
            },
            isActive = isBypassDNDActive
        )
        SongDurationSlider(
            songDuration = songDuration,
            onSongDurationChange = onSongDurationChange
        )
    }
}

@Composable
fun RemoveUnusedAppPermissionButton(
    activity: Activity
) {
    Button(
        onClick = {
            val intent = Intent()
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            val uri = Uri.fromParts("package", activity.packageName, null)
            intent.data = uri
            activity.startActivity(intent)
        },

        modifier = Modifier.padding(16.dp),
        colors = ButtonDefaults.buttonColors(
            Color.DarkGray
        ),
    ) {
        Text(
            text = "Remove Unused App Permission",
            color = Color.White,
            style = TextStyle(fontWeight = FontWeight.Bold)
        )
    }
}

@Composable
fun BypassDoNotDisturbButton(
    onActivationClick: () -> Unit,
    isActive: Boolean
) {
    Button(
        onClick = {
            onActivationClick()
        },

        modifier = Modifier.padding(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isActive) {
                Color.Red
            } else {
                Color.DarkGray
            }
        ),
    ) {
        Text(
            text = if (isActive) "Deactivate Bypass DND" else "Activate Bypass DND",
            color = Color.White,
            style = TextStyle(fontWeight = FontWeight.Bold)
        )
    }
}

@Composable
fun SongDurationSlider(
    songDuration: Int,
    onSongDurationChange: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(top = 10.dp, bottom = 10.dp, start = 30.dp, end = 30.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Song Duration $songDuration seconds")
        Slider(
            value = songDuration.toFloat(),
            onValueChange = { newValue ->
                onSongDurationChange(newValue.toInt())
            },
            valueRange = 1f..10f,
            steps = 9
        )
    }
}
