package com.amehran.expenselite.data.repository

import com.amehran.expenselite.data.local.PreferencesManager
import com.amehran.expenselite.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val preferencesManager: PreferencesManager,
) : SettingsRepository {

    override val isDarkMode: Flow<Boolean> = preferencesManager.isDarkMode

    override suspend fun setDarkMode(isDarkMode: Boolean) {
        preferencesManager.setDarkMode(isDarkMode)
    }
}
