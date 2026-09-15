package com.amehran.expenselite.presentation.navigation

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch

@Composable
fun AppShell(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
) {
    val scope = rememberCoroutineScope()
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry.value?.destination

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(12.dp))
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text("Dashboard") },
                    selected = currentDestination?.route?.contains("DashboardRoute") == true,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(AppRoute.DashboardRoute) {
                            popUpTo(AppRoute.DashboardRoute) { inclusive = false }
                            launchSingleTop = true
                        }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.DateRange, contentDescription = null) },
                    label = { Text("Analytics") },
                    selected = currentDestination?.route?.contains("AnalyticsRoute") == true,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(AppRoute.AnalyticsRoute) {
                            popUpTo(AppRoute.DashboardRoute) { inclusive = false }
                            launchSingleTop = true
                        }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                    label = { Text("Settings") },
                    selected = currentDestination?.route?.contains("SettingsRoute") == true,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(AppRoute.SettingsRoute) {
                            popUpTo(AppRoute.DashboardRoute) { inclusive = false }
                            launchSingleTop = true
                        }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                )
            }
        },
        modifier = modifier,
    ) {
        ExpenseNavHost(
            navController = navController,
            drawerState = drawerState,
        )
    }
}
