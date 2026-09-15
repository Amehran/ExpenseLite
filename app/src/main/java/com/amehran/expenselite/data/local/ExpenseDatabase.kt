package com.amehran.expenselite.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.amehran.expenselite.data.local.dao.CategoryDao
import com.amehran.expenselite.data.local.dao.ExpenseDao
import com.amehran.expenselite.data.local.entity.CategoryEntity
import com.amehran.expenselite.data.local.entity.ExpenseEntity

@Database(
    entities = [CategoryEntity::class, ExpenseEntity::class],
    version = 2,
    exportSchema = true,
)
abstract class ExpenseDatabase : RoomDatabase() {
    abstract val categoryDao: CategoryDao
    abstract val expenseDao: ExpenseDao

    companion object {
        const val DATABASE_NAME = "expense_db"

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE expenses ADD COLUMN categoryName TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE expenses ADD COLUMN categoryColorHex TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE categories ADD COLUMN colorHex TEXT NOT NULL DEFAULT ''")
            }
        }
    }
}
