package com.riviem.findmyphoneclap.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.riviem.findmyphoneclap.features.home.navigation.homeScreen
import com.riviem.findmyphoneclap.features.settings.navigation.settingsScreen

@Composable
fun MainNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = MainBottomDestination.Home.route,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        homeScreen()
        settingsScreen()
    }
}

