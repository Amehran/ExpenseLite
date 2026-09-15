package com.amehran.expenselite.presentation.navigation

sealed class Screen(val route: String) {
    data object Dashboard : Screen("dashboard")

    data object AddTransaction : Screen("add_transaction")

    data object CategoryManagement : Screen("categories")

    data object Analytics : Screen("analytics")

    data object Settings : Screen("settings")
}
