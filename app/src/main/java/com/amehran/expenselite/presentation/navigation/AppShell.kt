package com.amehran.expenselite.presentation.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.amehran.expenselite.ui.theme.Indigo600
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
                // Header with Logo, Title, and Navigation Close Button
                DrawerHeader(
                    onCloseClick = { scope.launch { drawerState.close() } },
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "MENU",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 28.dp, vertical = 4.dp),
                    fontWeight = FontWeight.Bold,
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Dashboard") },
                    label = { Text("Dashboard", fontWeight = FontWeight.SemiBold) },
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
                    icon = { Icon(Icons.Default.DateRange, contentDescription = "Analytics") },
                    label = { Text("Analytics", fontWeight = FontWeight.SemiBold) },
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
                    icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Categories") },
                    label = { Text("Manage Categories", fontWeight = FontWeight.SemiBold) },
                    selected = currentDestination?.route?.contains("CategoryRoute") == true,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(AppRoute.CategoryRoute) {
                            popUpTo(AppRoute.DashboardRoute) { inclusive = false }
                            launchSingleTop = true
                        }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                    label = { Text("Settings", fontWeight = FontWeight.SemiBold) },
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

                Spacer(modifier = Modifier.weight(1f))

                // Footer Caption
                Text(
                    text = "ExpenseLite v1.0 • Offline-First",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    modifier = Modifier.padding(28.dp),
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

@Composable
private fun DrawerHeader(onCloseClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = null,
                    tint = Indigo600,
                    modifier = Modifier.size(26.dp),
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "ExpenseLite",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = "Personal Finance",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        IconButton(onClick = onCloseClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Close Navigation Drawer",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
