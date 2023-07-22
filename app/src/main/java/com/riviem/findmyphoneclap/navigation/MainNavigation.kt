package com.riviem.findmyphoneclap.navigation

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination

@Composable
fun MainNavigation(
    mainAppState: MainAppState = rememberMainAppState(),
) {
    val items = getBottomNavItems()
    val navController = mainAppState.navController

    Scaffold(
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .height(106.dp),
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary,
            ) {
                items.forEach { destination ->
                    val selected =
                        mainAppState.currentDestination?.isTopLevelDestinationInHierarchy(
                            destination
                        ) ?: false
                    BottomNavigationItem(
                        icon = {
                            if (selected) {
                                Icon(
                                    imageVector = destination.selectedIcon,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSecondary,
                                    modifier = Modifier
                                        .size(30.dp)
                                )
                            } else {
                                Icon(
                                    imageVector = destination.unselectedIcon,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .alpha(0.5f)
                                        .blur(radius = 0.5.dp)
                                        .size(24.dp)
                                )
                            }
                        },
                        selected = selected,
                        onClick = {
                            navController.navigate(destination.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        },
    ) { innerPadding ->
        MainNavHost(
            navController = navController,
            modifier = Modifier
                .padding(innerPadding)
        )
    }
}

private fun NavDestination?.isTopLevelDestinationInHierarchy(destination: MainBottomDestination) =
    this?.hierarchy?.any {
        it.route?.contains(
            destination.route,
            true
        ) ?: false
    } ?: false

private fun getBottomNavItems(): List<MainBottomDestination> =
    listOf(
        MainBottomDestination.Home,
        MainBottomDestination.Settings,
    )
