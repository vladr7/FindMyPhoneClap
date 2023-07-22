package com.riviem.findmyphoneclap.features.home.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
        }
    )
}


@Composable
fun HomeScreen(
    sensitivity: Int,
    onSensitivityChange: (Int) -> Unit
) {
    Column {
        Text(
            text = "HomeScreen",
            fontSize = 30.sp,
        )
        SensitivitySlider(
            sensitivity = sensitivity,
            onSensitivityChange = onSensitivityChange
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
            .padding(start = 24.dp, end = 24.dp)
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