package com.amehran.expenselite.domain.repository

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val isDarkMode: Flow<Boolean>

    suspend fun setDarkMode(isDarkMode: Boolean)
}
