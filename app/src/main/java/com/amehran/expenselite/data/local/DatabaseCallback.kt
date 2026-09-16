package com.amehran.expenselite.data.local

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

class DatabaseCallback
@Inject
constructor(
    private val database: Provider<ExpenseDatabase>,
) : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)

        CoroutineScope(Dispatchers.IO).launch {
            val categoryDao = database.get().categoryDao

            // Seed immutable system categories
            categoryDao.insertCategory(
                com.amehran.expenselite.data.local.entity.CategoryEntity(
                    id = 1,
                    name = "Uncategorized",
                    iconResName = "ic_uncategorized",
                    isSystemDefault = true,
                    colorHex = "#9E9E9E",
                ),
            )
            categoryDao.insertCategory(
                com.amehran.expenselite.data.local.entity.CategoryEntity(
                    id = 2,
                    name = "Food",
                    iconResName = "ic_food",
                    isSystemDefault = true,
                    colorHex = "#4CAF50",
                ),
            )
            categoryDao.insertCategory(
                com.amehran.expenselite.data.local.entity.CategoryEntity(
                    id = 3,
                    name = "Transport",
                    iconResName = "ic_transport",
                    isSystemDefault = true,
                    colorHex = "#FF9800",
                ),
            )
            categoryDao.insertCategory(
                com.amehran.expenselite.data.local.entity.CategoryEntity(
                    id = 4,
                    name = "Entertainment",
                    iconResName = "ic_entertainment",
                    isSystemDefault = true,
                    colorHex = "#E91E63",
                ),
            )
            categoryDao.insertCategory(
                com.amehran.expenselite.data.local.entity.CategoryEntity(
                    id = 5,
                    name = "Bills",
                    iconResName = "ic_bills",
                    isSystemDefault = true,
                    colorHex = "#5C6BC0",
                ),
            )
            categoryDao.insertCategory(
                com.amehran.expenselite.data.local.entity.CategoryEntity(
                    id = 6,
                    name = "Income",
                    iconResName = "ic_income",
                    isSystemDefault = true,
                    colorHex = "#009688",
                ),
            )
        }
    }
}
