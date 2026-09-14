package com.amehran.expenselite.core.di

import android.app.Application
import androidx.room.Room
import com.amehran.expenselite.data.local.DatabaseCallback
import com.amehran.expenselite.data.local.ExpenseDatabase
import com.amehran.expenselite.data.local.dao.CategoryDao
import com.amehran.expenselite.data.local.dao.ExpenseDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideExpenseDatabase(
        app: Application,
        callback: DatabaseCallback
    ): ExpenseDatabase {
        return Room.databaseBuilder(
            app,
            ExpenseDatabase::class.java,
            ExpenseDatabase.DATABASE_NAME
        )
        .addCallback(callback)
        .build()
    }

    @Provides
    @Singleton
    fun provideCategoryDao(db: ExpenseDatabase): CategoryDao {
        return db.categoryDao
    }

    @Provides
    @Singleton
    fun provideExpenseDao(db: ExpenseDatabase): ExpenseDao {
        return db.expenseDao
    }
}
