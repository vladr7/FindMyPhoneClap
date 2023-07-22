package com.riviem.findmyphoneclap.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class MainBottomDestination(
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val route: String,
) {
    object Home : MainBottomDestination(
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home,
        route = "home",
    )

    object Settings : MainBottomDestination(
        selectedIcon = Icons.Filled.Settings,
        unselectedIcon = Icons.Outlined.Settings,
        "settings",
    )
}

