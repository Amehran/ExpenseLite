package com.amehran.expenselite.presentation.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
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

@Composable
fun ExpenseNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Dashboard.route,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier.fillMaxSize(),
    ) {
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onNavigateToAddTransaction = { navController.navigate(Screen.AddTransaction.route) },
                onNavigateToAnalytics = { navController.navigate(Screen.Analytics.route) },
            )
        }

        composable(Screen.AddTransaction.route) {
            AddTransactionScreen(
                onNavigateBack = { navController.popBackStack() },
            )
        }

        composable(Screen.CategoryManagement.route) {
            CategoryManagementScreen(
                onNavigateBack = { navController.popBackStack() },
            )
        }

        composable(Screen.Analytics.route) {
            AnalyticsScreen(
                onNavigateBack = { navController.popBackStack() },
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCategoryManagement = { navController.navigate(Screen.CategoryManagement.route) },
            )
        }
    }
}
