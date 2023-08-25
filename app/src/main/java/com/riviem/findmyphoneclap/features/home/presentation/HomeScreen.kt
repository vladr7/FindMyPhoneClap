package com.riviem.findmyphoneclap.features.home.presentation

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.riviem.findmyphoneclap.MainActivity
import com.riviem.findmyphoneclap.core.data.repository.audioclassification.SettingsRepositoryImpl
import com.riviem.findmyphoneclap.core.presentation.AlertDialog2Buttons
import com.riviem.findmyphoneclap.ui.theme.ActivateButtonColor
import kotlin.math.PI
import kotlin.math.atan2

@Composable
fun HomeRoute(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val activity = context as MainActivity

    HomeScreen(
        sensitivity = state.sensitivity,
        onSensitivityChange = { newValue ->
            viewModel.onSensitivityChange(newValue)
        },
        onActivationClick = {
            viewModel.configureService()
        },
        isServiceActive = state.isServiceActivated,
        volume = state.volume,
        onVolumeChange = { newValue ->
            viewModel.onVolumeChange(newValue)
        },
        songDuration = state.songDuration,
        onSongDurationChange = { newValue ->
            viewModel.onSongDurationChange(newValue)
        },
        onBypassDoNotDisturbClick = {
            viewModel.onBypassDoNotDisturbClick()
        },
        isBypassDNDActive = state.isBypassDNDActive,
        shouldAskForMicrophonePermission = state.shouldAskForMicrophonePermission,
        onMicrophonePermissionFlowDone = {
            viewModel.resetShouldAskForMicrophonePermission()
        },
        activity = activity,
        context = context,
        pauseDuration = state.pauseDuration,
        onPauseDurationChange = { newValue ->
            viewModel.onPauseDurationChange(newValue)
        }
    )
}


@Composable
fun HomeScreen(
    sensitivity: Int,
    onSensitivityChange: (Int) -> Unit,
    onActivationClick: () -> Unit,
    isServiceActive: Boolean,
    volume: Int,
    onVolumeChange: (Int) -> Unit,
    songDuration: Int,
    onSongDurationChange: (Int) -> Unit,
    onBypassDoNotDisturbClick: () -> Unit,
    isBypassDNDActive: Boolean,
    shouldAskForMicrophonePermission: Boolean,
    onMicrophonePermissionFlowDone: () -> Unit,
    activity: Activity,
    context: Context,
    pauseDuration: Int,
    onPauseDurationChange: (Int) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ActivateServiceContent()
//        ActivationButton(
//            onActivationClick = onActivationClick,
//            isActive = isServiceActive
//        )
//        SensitivitySlider(
//            sensitivity = sensitivity,
//            onSensitivityChange = onSensitivityChange
//        )
//        VolumeSlider(
//            volume = volume,
//            onVolumeChange = onVolumeChange
//        )
//        SongDurationSlider(
//            songDuration = songDuration,
//            onSongDurationChange = onSongDurationChange
//        )
//        BypassDoNotDisturbButton(
//            onActivationClick = onBypassDoNotDisturbClick,
//            isActive = isBypassDNDActive
//        )
//        if (shouldAskForMicrophonePermission) {
//            MicrophonePermissionDialog(
//                activity = activity,
//                context = context,
//                onMicrophonePermissionDismissed = onMicrophonePermissionFlowDone,
//            )
//        }
//        RemoveUnusedAppPermissionButton(
//            activity = activity
//        )
//        PauseForDuration(duration = pauseDuration, onDurationChange = onPauseDurationChange)
    }
}

@Composable
fun ActivateServiceContent(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    var redSliderValue by remember { mutableFloatStateOf(1f) }
    var blueSliderValue by remember { mutableFloatStateOf(1f) }
    var draggingRedSlider by remember { mutableStateOf(false) }
    var draggingBlueSlider by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .padding(top = 100.dp)
            .size(200.dp)
    ) {
        Canvas(
            modifier = Modifier
                .matchParentSize()
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { startOffset ->

                        },
                        onDrag = { change, dragAmount ->
                            val width = size.width
                            val height = size.height
                            val touchPoint = change.position
                            val center = Offset((width / 2).toFloat(), (height / 2).toFloat())
                            val angle = atan2(
                                center.y - touchPoint.y,
                                touchPoint.x - center.x
                            ) * (180.0 / PI).toFloat()

                            if (angle in 90.0..180.0) {
                                if (!draggingBlueSlider) {
                                    val newRedValue = 1f - (angle - 90f) / 90f
                                    redSliderValue = newRedValue
                                    draggingRedSlider = true
                                }
                            } else if (angle in 0.0..90.0) {
                                if(!draggingRedSlider) {
                                    val newBlueValue = angle / 90f
                                    blueSliderValue = newBlueValue
                                    draggingBlueSlider = true
                                }
                            }
                        },

                        onDragEnd = {
                            draggingBlueSlider = false
                            draggingRedSlider = false
                        }
                    )
                }
        ) {
            volumeAndSensitivitySliders(redSliderValue, blueSliderValue)
        }
        StartServiceButton(
            modifier = modifier,
            onClick = onClick
        )
    }
}


private fun DrawScope.volumeAndSensitivitySliders(
    redSliderValue: Float,
    blueSliderValue: Float,
    sliderWidth: Float = 20.dp.toPx(),
    sliderAngle: Float = 75f,
) {
    val gradient = Brush.radialGradient(
        colors = listOf(
            ActivateButtonColor.copy(alpha = 0.5f),
            Color.Transparent
        ),
        center = Offset(size.width / 2, size.height / 2),
        radius = size.width / 2
    )
    drawCircle(
        brush = gradient,
        center = Offset(size.width / 2, size.height / 2),
        radius = size.width / 2
    )

    drawArc(
        brush = Brush.verticalGradient(
            colors = listOf(Color.Red, Color.White),
            startY = 0f,
            endY = size.height
        ),
        startAngle = 180f,
        sweepAngle = sliderAngle * redSliderValue,
        useCenter = false,
        size = size,
        topLeft = Offset(0f, 0f),
        style = Stroke(width = sliderWidth, cap = StrokeCap.Round)
    )

    drawArc(
        brush = Brush.verticalGradient(
            colors = listOf(Color.Blue, Color.White),
            startY = 0f,
            endY = size.height
        ),
        startAngle = 0f,
        sweepAngle = -sliderAngle * blueSliderValue,
        useCenter = false,
        size = size,
        topLeft = Offset(0f, 0f),
        style = Stroke(width = sliderWidth, cap = StrokeCap.Round)
    )
}

@Composable
private fun BoxScope.StartServiceButton(onClick: () -> Unit, modifier: Modifier) {
    Button(
        onClick = onClick,
        modifier = Modifier.Companion
            .align(Alignment.Center)
            .size(100.dp),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = ActivateButtonColor,
            contentColor = Color.White
        ),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.5f))
    ) {
        Icon(
            Icons.Default.PlayArrow, contentDescription = "Activate Service",
            modifier = modifier.size(40.dp)
        )
    }
}


//@Composable
//fun ActivateServiceButton(
//    modifier: Modifier = Modifier,
//    onClick: () -> Unit = {}
//) {
//    Box(
//        modifier = modifier
//            .padding(top = 30.dp)
//            .size(200.dp)
//    ) {
//        Canvas(modifier = Modifier.matchParentSize()) {
//            val gradient = Brush.radialGradient(
//                colors = listOf(
//                    ActivateButtonColor.copy(alpha = 0.5f),
//                    Color.Transparent
//                ),
//                center = Offset(size.width / 2, size.height / 2),
//                radius = size.width / 2
//            )
//            drawCircle(
//                brush = gradient,
//                center = Offset(size.width / 2, size.height / 2),
//                radius = size.width / 2
//            )
//        }
//
//        Button(
//            onClick = onClick,
//            modifier = Modifier
//                .align(Alignment.Center)
//                .size(100.dp),
//            shape = CircleShape,
//            colors = ButtonDefaults.buttonColors(
//                containerColor = ActivateButtonColor,
//                contentColor = Color.White
//            ),
//            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.5f))
//        ) {
//            Icon(
//                Icons.Default.PlayArrow, contentDescription = "Activate Service",
//                modifier = modifier.size(40.dp)
//            )
//        }
//    }
//}


@Composable
fun PauseForDuration(
    duration: Int,
    onDurationChange: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(top = 24.dp)
    ) {
        Text(
            text = "Pause for $duration seconds",
            fontSize = 20.sp,
            modifier = Modifier
                .padding(bottom = 8.dp)
        )
        Slider(
            value = duration.toFloat(),
            onValueChange = { newValue ->
                onDurationChange(newValue.toInt())
            },
            valueRange = 1f..10f,
            steps = 9,
            modifier = Modifier
                .padding(horizontal = 16.dp)
        )
    }
}

@Composable
fun MicrophonePermissionDialog(
    context: Context,
    activity: Activity,
    onMicrophonePermissionDismissed: () -> Unit,
) {
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
            AlertDialog2Buttons(
                title = "Microphone Permission required to use this app",
                dismissText = "Cancel",
                confirmText = "OK",
                onDismissClick = onMicrophonePermissionDismissed,
                onConfirmClick = {
                    val intent = Intent()
                    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    val uri = Uri.fromParts(
                        "package",
                        activity.packageName,
                        null
                    )
                    intent.data = uri
                    activity.startActivity(intent)
                    onMicrophonePermissionDismissed()
                }
            )
        } else {
            onMicrophonePermissionDismissed()
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                SettingsRepositoryImpl.MY_PERMISSIONS_REQUEST_RECORD_AUDIO
            )
        }
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
            Color.Green
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
                Color.Green
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
fun ActivationButton(
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
                Color.Green
            }
        ),
    ) {
        Text(
            text = if (isActive) "Deactivate" else "Activate",
            color = Color.White,
            style = TextStyle(fontWeight = FontWeight.Bold)
        )
    }
}

@Composable
fun SensitivitySlider(
    sensitivity: Int,
    onSensitivityChange: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(top = 10.dp, bottom = 10.dp, start = 30.dp, end = 30.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Sensitivity $sensitivity")
        Slider(
            value = sensitivity.toFloat(),
            onValueChange = { newValue ->
                onSensitivityChange(newValue.toInt())
            },
            valueRange = 5f..100f,
            steps = 100
        )
    }
}

@Composable
fun VolumeSlider(
    volume: Int,
    onVolumeChange: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(top = 10.dp, bottom = 10.dp, start = 30.dp, end = 30.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Volume $volume")
        Slider(
            value = volume.toFloat(),
            onValueChange = { newValue ->
                onVolumeChange(newValue.toInt())
            },
            valueRange = 0f..100f,
            steps = 100
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