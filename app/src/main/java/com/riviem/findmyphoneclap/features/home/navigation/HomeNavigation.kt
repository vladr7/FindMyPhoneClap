package com.riviem.findmyphoneclap.features.home.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.riviem.findmyphoneclap.features.home.presentation.HomeRoute
import com.riviem.findmyphoneclap.navigation.MainBottomDestination

fun NavController.navigateToHome(navOptions: NavOptions? = null) {
    this.navigate(MainBottomDestination.Home.route, navOptions)
}

fun NavGraphBuilder.homeScreen() {
    composable(route = MainBottomDestination.Home.route) {
        HomeRoute()
    }
}
