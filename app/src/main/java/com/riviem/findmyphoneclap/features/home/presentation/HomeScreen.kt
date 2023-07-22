package com.riviem.findmyphoneclap.features.home.presentation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp

@Composable
fun HomeRoute() {
    HomeScreen()
}


@Composable
fun HomeScreen() {
    Text(
        text = "HomeScreen",
        fontSize = 30.sp,
    )
}