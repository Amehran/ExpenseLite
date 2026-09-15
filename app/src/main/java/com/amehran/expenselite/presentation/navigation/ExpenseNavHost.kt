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
) {
    NavHost(
        navController = navController,
        startDestination = AppRoute.DashboardRoute,
        modifier = modifier.fillMaxSize(),
    ) {
        composable<AppRoute.DashboardRoute> {
            DashboardScreen(
                onNavigateToAddTransaction = { navController.navigate(AppRoute.AddEditRoute()) },
                onNavigateToAnalytics = { navController.navigate(AppRoute.AnalyticsRoute) },
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
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCategoryManagement = { navController.navigate(AppRoute.CategoryRoute) },
            )
        }
    }
}
