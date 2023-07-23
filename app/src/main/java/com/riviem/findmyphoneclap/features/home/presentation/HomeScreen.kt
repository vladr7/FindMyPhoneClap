package com.riviem.findmyphoneclap.features.home.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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

@Composable
fun HomeRoute(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    HomeScreen(
        sensitivity = state.sensitivity,
        onSensitivityChange = { newValue ->
            viewModel.onSensitivityChange(newValue)
        },
        onActivationClick = {
            viewModel.configureService()
        },
        isActive = state.isServiceActivated,
        volume = state.volume,
        onVolumeChange = { newValue ->
            viewModel.onVolumeChange(newValue)
        }
    )
}


@Composable
fun HomeScreen(
    sensitivity: Int,
    onSensitivityChange: (Int) -> Unit,
    onActivationClick: () -> Unit,
    isActive: Boolean,
    volume: Int,
    onVolumeChange: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "HomeScreen",
            fontSize = 30.sp,
            modifier = Modifier
                .padding(top = 24.dp)
        )
        ActivationButton(
            onActivationClick = onActivationClick,
            isActive = isActive
        )
        SensitivitySlider(
            sensitivity = sensitivity,
            onSensitivityChange = onSensitivityChange
        )
        VolumeSlider(
            volume = volume,
            onVolumeChange = onVolumeChange
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
            .padding(30.dp),
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
            .padding(30.dp),
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