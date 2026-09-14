package com.amehran.expenselite.presentation.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.amehran.expenselite.presentation.dashboard.DashboardScreen
import com.amehran.expenselite.presentation.transaction.AddTransactionScreen

@Composable
fun ExpenseNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Dashboard.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier.fillMaxSize()
    ) {
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onNavigateToAddTransaction = { navController.navigate(Screen.AddTransaction.route) }
            )
        }
        
        composable(Screen.AddTransaction.route) {
            AddTransactionScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.CategoryManagement.route) {
            Text("Category Management (Coming Soon)")
        }
        
        composable(Screen.Analytics.route) {
            Text("Analytics (Coming Soon)")
        }
        
        composable(Screen.Settings.route) {
            Text("Settings (Coming Soon)")
        }
    }
}
