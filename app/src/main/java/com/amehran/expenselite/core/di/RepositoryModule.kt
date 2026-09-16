package com.amehran.expenselite.core.di

import com.amehran.expenselite.data.repository.SettingsRepositoryImpl
import com.amehran.expenselite.data.repository.TransactionRepositoryImpl
import com.amehran.expenselite.domain.repository.SettingsRepository
import com.amehran.expenselite.domain.repository.TransactionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindTransactionRepository(
        transactionRepositoryImpl: TransactionRepositoryImpl,
    ): TransactionRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(
        settingsRepositoryImpl: SettingsRepositoryImpl,
    ): SettingsRepository
}
