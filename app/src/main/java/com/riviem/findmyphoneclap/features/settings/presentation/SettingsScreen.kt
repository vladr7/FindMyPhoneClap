package com.riviem.findmyphoneclap.features.settings.presentation

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DoDisturbOn
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.riviem.findmyphoneclap.MainActivity
import com.riviem.findmyphoneclap.features.home.presentation.GradientBackgroundScreen
import com.riviem.findmyphoneclap.ui.theme.SettingsActivateSwitchButtonColor
import com.riviem.findmyphoneclap.ui.theme.SettingsDisabledSwitchBorderColor
import com.riviem.findmyphoneclap.ui.theme.SettingsDisabledSwitchButtonColor
import com.riviem.findmyphoneclap.ui.theme.SettingsDisabledSwitchIconColor
import com.riviem.findmyphoneclap.ui.theme.SettingsDisabledSwitchTrackColor
import com.riviem.findmyphoneclap.ui.theme.SettingsInactiveSwitchBorderColor
import com.riviem.findmyphoneclap.ui.theme.SettingsInactiveSwitchButtonColor
import com.riviem.findmyphoneclap.ui.theme.SettingsInactiveSwitchIconColor
import com.riviem.findmyphoneclap.ui.theme.SettingsInactiveSwitchTrackColor
import com.riviem.findmyphoneclap.ui.theme.SettingsVolumeIconColor

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
    GradientBackgroundScreen {
        Column(
            modifier = Modifier
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            SettingsTitle(
                modifier = Modifier
            )
            SettingsActivateButton(
                activated = isBypassDNDActive,
                onClick = {
                    onBypassDoNotDisturbClick()
                },
                modifier = Modifier,
                title = "Override Mute",
                subtitle = "Play sound even if phone is muted",
                startIcon = Icons.Default.VolumeUp,
                startIconColor = SettingsVolumeIconColor,
            )
//        RemoveUnusedAppPermissionButton(activity = activity)
//        SongDurationSlider(
//            songDuration = songDuration,
//            onSongDurationChange = onSongDurationChange
//        )
        }
    }
}

@Composable
fun SettingsTitle(
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 50.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Settings",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = modifier
        )
    }
}

@Composable
fun SettingsActivateButton(
    modifier: Modifier = Modifier,
    activated: Boolean,
    onClick: () -> Unit,
    title: String,
    subtitle: String,
    startIcon: ImageVector,
    startIconColor: Color,
) {

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(110.dp)
            .border(
                1.dp,
                Color.Gray.copy(alpha = 0.7f),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Image(
            imageVector = startIcon,
            contentDescription = null,
            modifier = modifier
                .padding(start = 8.dp)
                .size(40.dp),
            colorFilter = ColorFilter.tint(startIconColor)
        )
        Column(
            modifier = modifier
                .padding(start = 16.dp, end = 16.dp)
                .weight(0.8f, fill = false),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = subtitle,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                overflow = TextOverflow.Ellipsis,
                maxLines = 2
            )
        }

        Spacer(modifier = modifier.weight(0.05f))
        Switch(
            modifier = modifier
                .weight(0.15f)
                .padding(end = 8.dp),
            checked = activated,
            onCheckedChange = { onClick() },
            colors = SwitchColors(
                checkedThumbColor = SettingsActivateSwitchButtonColor,
                checkedBorderColor = SettingsActivateSwitchButtonColor,
                checkedTrackColor = SettingsActivateSwitchButtonColor.copy(alpha = 0.7f),
                checkedIconColor = Color.White,
                uncheckedThumbColor = SettingsInactiveSwitchButtonColor,
                uncheckedTrackColor = SettingsInactiveSwitchTrackColor,
                uncheckedBorderColor = SettingsInactiveSwitchBorderColor,
                uncheckedIconColor = SettingsInactiveSwitchIconColor,
                disabledCheckedThumbColor = SettingsDisabledSwitchButtonColor,
                disabledCheckedTrackColor = SettingsDisabledSwitchTrackColor,
                disabledCheckedBorderColor = SettingsDisabledSwitchBorderColor,
                disabledCheckedIconColor = SettingsDisabledSwitchIconColor,
                disabledUncheckedThumbColor = SettingsDisabledSwitchButtonColor,
                disabledUncheckedTrackColor = SettingsDisabledSwitchTrackColor,
                disabledUncheckedBorderColor = SettingsDisabledSwitchBorderColor,
                disabledUncheckedIconColor = SettingsDisabledSwitchIconColor
            )
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
