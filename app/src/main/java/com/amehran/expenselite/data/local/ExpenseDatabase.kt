package com.amehran.expenselite.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.amehran.expenselite.data.local.dao.CategoryDao
import com.amehran.expenselite.data.local.dao.ExpenseDao
import com.amehran.expenselite.data.local.entity.CategoryEntity
import com.amehran.expenselite.data.local.entity.ExpenseEntity

@Database(
    entities = [CategoryEntity::class, ExpenseEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class ExpenseDatabase : RoomDatabase() {
    abstract val categoryDao: CategoryDao
    abstract val expenseDao: ExpenseDao

    companion object {
        const val DATABASE_NAME = "expense_db"
    }
}
