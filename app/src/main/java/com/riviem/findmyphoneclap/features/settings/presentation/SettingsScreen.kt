package com.riviem.findmyphoneclap.features.settings.presentation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp

@Composable
fun SettingsRoute() {
    SettingsScreen()
}

@Composable
fun SettingsScreen() {
    Text(
        text = "SettingsScreen",
        fontSize = 30.sp,
    )
}