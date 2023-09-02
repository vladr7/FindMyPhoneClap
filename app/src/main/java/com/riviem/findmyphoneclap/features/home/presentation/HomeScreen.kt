package com.riviem.findmyphoneclap.features.home.presentation

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Path
import android.graphics.Typeface
import android.net.Uri
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.riviem.findmyphoneclap.MainActivity
import com.riviem.findmyphoneclap.R
import com.riviem.findmyphoneclap.core.data.repository.audioclassification.SettingsRepositoryImpl
import com.riviem.findmyphoneclap.core.presentation.AlertDialog2Buttons
import com.riviem.findmyphoneclap.core.presentation.animations.PulsatingCircle
import com.riviem.findmyphoneclap.ui.theme.ActivateButtonColor
import com.riviem.findmyphoneclap.ui.theme.BackgroundBottomColor
import com.riviem.findmyphoneclap.ui.theme.BackgroundTopColor
import com.riviem.findmyphoneclap.ui.theme.DeactivateButtonColor
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

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
    volume: Int,
    onVolumeChange: (Int) -> Unit,
    onActivationClick: () -> Unit,
    isServiceActive: Boolean,
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
    GradientBackgroundScreen {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            GreetingText(
                modifier = Modifier
                    .padding(top = 30.dp)
            )
            Spacer(
                modifier = Modifier.weight(1f)
            )
            ActivateServiceContent(
                modifier = Modifier,
                onClick = onActivationClick,
                isServiceActive = isServiceActive,
                volume = volume,
                onVolumeChange = onVolumeChange,
                sensitivity = sensitivity,
                onSensitivityChange = onSensitivityChange,
            )
            Spacer(
                modifier = Modifier.weight(1f)
            )
        }
    }

}

@Composable
fun GreetingText(
    modifier: Modifier = Modifier
) {
    BasicText(
        text = stringResource(R.string.welcome),
        style = TextStyle(
            fontSize = 30.sp,
            color = Color.White
        ),
        modifier = modifier
    )
}

@Composable
fun ActivateServiceContent(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    isServiceActive: Boolean,
    volume: Int,
    onVolumeChange: (Int) -> Unit,
    sensitivity: Int,
    onSensitivityChange: (Int) -> Unit
) {
    Box(
        modifier = modifier
            .size(200.dp)
    ) {
        Sliders(
            isServiceActive = isServiceActive,
            volume = volume,
            onVolumeChange = onVolumeChange,
            sensitivity = sensitivity,
            onSensitivityChange = onSensitivityChange
        )
        if (isServiceActive) {
            PulsatingCircle(
                modifier = Modifier,
                scale = 400f,
            )
        }
        GlowStartServiceButton(isServiceActive)
        StartServiceButton(
            modifier = Modifier,
            onClick = onClick,
            isServiceActive = isServiceActive
        )
    }
}

@Composable
private fun BoxScope.GlowStartServiceButton(isServiceActive: Boolean) {
    Canvas(
        modifier = Modifier.Companion
            .matchParentSize()
    ) {
        val color = if (!isServiceActive) ActivateButtonColor else DeactivateButtonColor
        val gradient = Brush.radialGradient(
            colors = listOf(
                color.copy(alpha = 0.5f),
                Color.Transparent
            ),
            center = Offset(size.width / 2, size.height / 2),
            radius = size.width / 2 + 50f
        )
        drawCircle(
            brush = gradient,
            center = Offset(size.width / 2, size.height / 2),
            radius = size.width / 2 + 50f
        )
    }
}

@Composable
private fun BoxScope.Sliders(
    isServiceActive: Boolean,
    sensitivity: Int,
    onSensitivityChange: (Int) -> Unit,
    volume: Int,
    onVolumeChange: (Int) -> Unit,
) {
    var draggingRedSlider by remember { mutableStateOf(false) }
    var draggingBlueSlider by remember { mutableStateOf(false) }
    val redSliderValue = maxOf(0.1f, volume / 100f)
    val blueSliderValue = maxOf(0.1f, sensitivity / 100f)
    val animatedRedColor by animateColorAsState(
        targetValue = if (redSliderValue > 0.9f) Color.Red else Color(
            0xFFff006e
        ), label = ""
    )
    val animatedBlueColor by animateColorAsState(
        targetValue = if (blueSliderValue > 0.9f) Color(
            0xFF0077b6
        ) else Color.Blue, label = ""
    )

    AnimatedVisibility(
        modifier = Modifier
            .size(200.dp),
        visible = isServiceActive,
        enter = scaleIn(),
        exit = scaleOut()
    ) {
        Canvas(
            modifier = Modifier.Companion
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
                                    onVolumeChange((newRedValue * 100).toInt())
                                    draggingRedSlider = true
                                }
                            } else if (angle in 0.0..90.0) {
                                if (!draggingRedSlider) {
                                    val newBlueValue = angle / 90f
                                    onSensitivityChange((newBlueValue * 100).toInt())
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
            volumeSensitivitySliders(
                redSliderValue = redSliderValue,
                blueSliderValue = blueSliderValue,
                animatedRedColor = animatedRedColor,
                animatedBlueColor = animatedBlueColor,
            )
        }
    }
}

private fun DrawScope.volumeSensitivitySliders(
    redSliderValue: Float,
    blueSliderValue: Float,
    animatedRedColor: Color,
    animatedBlueColor: Color,
    sliderWidth: Float = 27.dp.toPx(),
    sliderAngle: Float = 75f,
    slidersOuterPadding: Float = -12.dp.toPx()
) {
    sliders(
        animatedRedColor,
        sliderAngle,
        redSliderValue,
        sliderWidth,
        animatedBlueColor,
        blueSliderValue,
        slidersOuterPadding
    )
    textOnSliders(sliderAngle, redSliderValue, blueSliderValue)
    textValuesOnTheSideOfSliders(
        sliderAngle,
        redSliderValue,
        blueSliderValue,
        animatedRedColor,
        animatedBlueColor
    )
}

private fun DrawScope.textValuesOnTheSideOfSliders(
    sliderAngle: Float,
    redSliderValue: Float,
    blueSliderValue: Float,
    animatedRedColor: Color,
    animatedBlueColor: Color
) {
    val baseTextSize = 50f
    val textOffsetRadius = 85f

    val paintRed = android.graphics.Paint().apply {
        isAntiAlias = true
        textSize = baseTextSize
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textAlign = android.graphics.Paint.Align.CENTER
        color = animatedRedColor.toArgb()
    }

    val paintBlue = android.graphics.Paint().apply {
        isAntiAlias = true
        textSize = baseTextSize
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textAlign = android.graphics.Paint.Align.CENTER
        color = ActivateButtonColor.toArgb()
    }

    val centerX = size.width / 2
    val centerY = size.height / 2

    val redSliderRadians = Math.toRadians((180 + sliderAngle * redSliderValue).toDouble()).toFloat()
    val blueSliderRadians =
        Math.toRadians((360 - sliderAngle * blueSliderValue).toDouble()).toFloat()

    val redX = centerX + (centerX + textOffsetRadius) * cos(redSliderRadians)
    val redY = centerY + (centerY + textOffsetRadius) * sin(redSliderRadians)

    val blueX = centerX + (centerX + textOffsetRadius) * cos(blueSliderRadians)
    val blueY = centerY + (centerY + textOffsetRadius) * sin(blueSliderRadians)

    drawIntoCanvas { canvas ->
        canvas.nativeCanvas.drawText(
            "${
                roundUpToEven((redSliderValue * 100).toInt())
            }", redX, redY, paintRed
        )
        canvas.nativeCanvas.drawText(
            "${
                roundUpToEven((blueSliderValue * 100).toInt())
            }", blueX, blueY, paintBlue
        )
    }
}

fun roundUpToEven(number: Int): Int {
    return if (number % 2 == 0) number else number + 1
}


private fun DrawScope.sliders(
    animatedRedColor: Color,
    sliderAngle: Float,
    redSliderValue: Float,
    sliderWidth: Float,
    animatedBlueColor: Color,
    blueSliderValue: Float,
    outerPadding: Float
) {
    val newSize = Size(size.width - outerPadding, size.height - outerPadding)
    val newTopLeft = Offset(outerPadding / 2, outerPadding / 2)

    drawArc(
        brush = Brush.verticalGradient(
            colors = listOf(animatedRedColor, Color.White),
            startY = 0f,
            endY = newSize.height
        ),
        startAngle = 180f,
        sweepAngle = sliderAngle * redSliderValue,
        useCenter = false,
        size = newSize,
        topLeft = newTopLeft,
        style = Stroke(width = sliderWidth, cap = StrokeCap.Round)
    )

    drawArc(
        brush = Brush.verticalGradient(
            colors = listOf(animatedBlueColor, Color.White),
            startY = 0f,
            endY = newSize.height
        ),
        startAngle = 0f,
        sweepAngle = -sliderAngle * blueSliderValue,
        useCenter = false,
        size = newSize,
        topLeft = newTopLeft,
        style = Stroke(width = sliderWidth, cap = StrokeCap.Round)
    )
}


private fun DrawScope.textOnSliders(
    sliderAngle: Float,
    redSliderValue: Float,
    blueSliderValue: Float
) {
    val baseTextSize = 56f
    val minTextSize = baseTextSize * 0.4f
    val redTextSize = minTextSize + (baseTextSize - minTextSize) * redSliderValue
    val blueTextSize = minTextSize + (baseTextSize - minTextSize) * blueSliderValue

    val paintRed = android.graphics.Paint().apply {
        isAntiAlias = true
        textSize = redTextSize
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textAlign = android.graphics.Paint.Align.CENTER
        color = android.graphics.Color.WHITE
    }

    val paintBlue = android.graphics.Paint().apply {
        isAntiAlias = true
        textSize = blueTextSize
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textAlign = android.graphics.Paint.Align.CENTER
        color = android.graphics.Color.WHITE
    }

    drawIntoCanvas { canvas ->
        val pathRed = Path().apply {
            addArc(
                0f,
                0f,
                size.width,
                size.height,
                180f,
                sliderAngle * redSliderValue
            )
        }
        canvas.nativeCanvas.drawTextOnPath("Volume", pathRed, 0f, 0f, paintRed)

        val pathBlue = Path().apply {
            addArc(
                0f,
                0f,
                size.width,
                size.height,
                360f - (sliderAngle * blueSliderValue),
                sliderAngle * blueSliderValue
            )
        }
        canvas.nativeCanvas.drawTextOnPath("Sensitivity", pathBlue, 0f, 0f, paintBlue)
    }
}


@Composable
private fun BoxScope.StartServiceButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isServiceActive: Boolean
) {

    val gradientBrush = Brush.linearGradient(
        colors = listOf(
            if (!isServiceActive) ActivateButtonColor else DeactivateButtonColor,
            Color.White
        ),

        )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(110.dp)
            .align(Alignment.Center)
            .background(brush = gradientBrush, shape = CircleShape)
    ) {
        Button(
            onClick = onClick,
            modifier = Modifier.fillMaxSize(),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = Color.White
            ),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.5f))
        ) {
            Icon(
                imageVector = if (!isServiceActive) Icons.Default.PlayArrow else Icons.Default.Stop,
                contentDescription = "Activate Service",
                modifier = Modifier.size(40.dp)
            )
        }
    }


}


@Composable
fun GradientBackgroundScreen(
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        BackgroundTopColor,
                        BackgroundBottomColor
                    )
                )
            )
    ) {
        content()
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

