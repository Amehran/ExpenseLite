package com.amehran.expenselite.presentation.navigation

import kotlinx.serialization.Serializable

sealed interface AppRoute {
    @Serializable
    data object DashboardRoute : AppRoute

    @Serializable
    data object AnalyticsRoute : AppRoute

    @Serializable
    data object SettingsRoute : AppRoute

    @Serializable
    data object CategoryRoute : AppRoute

    @Serializable
    data class AddEditRoute(val expenseId: String? = null) : AppRoute
}
