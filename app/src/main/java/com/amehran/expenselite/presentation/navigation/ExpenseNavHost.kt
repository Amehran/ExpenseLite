package com.amehran.expenselite.presentation.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.amehran.expenselite.presentation.analytics.AnalyticsScreen
import com.amehran.expenselite.presentation.category.CategoryManagementScreen
import com.amehran.expenselite.presentation.dashboard.DashboardScreen
import com.amehran.expenselite.presentation.settings.SettingsScreen
import com.amehran.expenselite.presentation.transaction.AddTransactionScreen
import kotlinx.coroutines.launch

@Composable
fun ExpenseNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
) {
    val scope = rememberCoroutineScope()

    NavHost(
        navController = navController,
        startDestination = AppRoute.DashboardRoute,
        modifier = modifier.fillMaxSize(),
    ) {
        composable<AppRoute.DashboardRoute> {
            DashboardScreen(
                onNavigateToAddTransaction = { navController.navigate(AppRoute.AddEditRoute()) },
                onEditTransaction = { id -> navController.navigate(AppRoute.AddEditRoute(expenseId = id.toString())) },
                onNavigateToAnalytics = { navController.navigate(AppRoute.AnalyticsRoute) },
                onOpenDrawer = { scope.launch { drawerState.open() } },
            )
        }

        composable<AppRoute.AddEditRoute> {
            // Note: If you need to read expenseId, it's inside it.toRoute<AppRoute.AddEditRoute>().expenseId
            AddTransactionScreen(
                onNavigateBack = { navController.popBackStack() },
            )
        }

        composable<AppRoute.CategoryRoute> {
            CategoryManagementScreen(
                onNavigateBack = { navController.popBackStack() },
            )
        }

        composable<AppRoute.AnalyticsRoute> {
            AnalyticsScreen(
                onNavigateBack = { navController.popBackStack() },
            )
        }

        composable<AppRoute.SettingsRoute> {
            SettingsScreen(
                onOpenDrawer = { scope.launch { drawerState.open() } },
                onNavigateToCategoryManagement = { navController.navigate(AppRoute.CategoryRoute) },
            )
        }
    }
}
