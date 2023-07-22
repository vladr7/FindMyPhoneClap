package com.riviem.findmyphoneclap.features.settings.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.riviem.findmyphoneclap.features.settings.presentation.SettingsRoute
import com.riviem.findmyphoneclap.navigation.MainBottomDestination

fun NavController.navigateToSettings(navOptions: NavOptions? = null) {
    this.navigate(MainBottomDestination.Settings.route, navOptions)
}

fun NavGraphBuilder.settingsScreen() {
    composable(route = MainBottomDestination.Settings.route) {
        SettingsRoute()
    }
}
