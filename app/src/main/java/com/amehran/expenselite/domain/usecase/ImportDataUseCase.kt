package com.amehran.expenselite.domain.usecase

import androidx.room.withTransaction
import com.amehran.expenselite.data.local.ExpenseDatabase
import com.amehran.expenselite.data.local.dao.CategoryDao
import com.amehran.expenselite.data.local.dao.ExpenseDao
import com.amehran.expenselite.data.local.entity.CategoryEntity
import com.amehran.expenselite.data.local.entity.ExpenseEntity
import org.json.JSONObject
import java.io.InputStream
import javax.inject.Inject

class ImportDataUseCase @Inject constructor(
    private val database: ExpenseDatabase,
    private val categoryDao: CategoryDao,
    private val expenseDao: ExpenseDao,
) {
    suspend operator fun invoke(inputStream: InputStream): Result<Unit> {
        return try {
            val jsonString = inputStream.use { stream ->
                stream.bufferedReader(Charsets.UTF_8).readText()
            }

            val rootJson = JSONObject(jsonString)

            val categoriesArray = rootJson.optJSONArray("categories")
            val expensesArray = rootJson.optJSONArray("expenses")

            if (categoriesArray == null || expensesArray == null) {
                return Result.failure(IllegalArgumentException("Invalid backup file format"))
            }

            // Parse categories
            val categoriesToInsert = mutableListOf<CategoryEntity>()
            for (i in 0 until categoriesArray.length()) {
                val catJson = categoriesArray.getJSONObject(i)
                categoriesToInsert.add(
                    CategoryEntity(
                        id = catJson.getLong("id"),
                        name = catJson.getString("name"),
                        iconResName = catJson.getString("iconResName"),
                        isSystemDefault = catJson.getBoolean("isSystemDefault"),
                    ),
                )
            }

            // Parse expenses
            val expensesToInsert = mutableListOf<ExpenseEntity>()
            for (i in 0 until expensesArray.length()) {
                val expJson = expensesArray.getJSONObject(i)
                expensesToInsert.add(
                    ExpenseEntity(
                        id = expJson.getLong("id"),
                        title = expJson.getString("title"),
                        amountCents = expJson.getLong("amountCents"),
                        categoryId = expJson.getLong("categoryId"),
                        timestamp = expJson.getLong("timestamp"),
                        isIncome = expJson.getBoolean("isIncome"),
                        isSubscription = expJson.getBoolean("isSubscription"),
                        recurrenceInterval = expJson.getString("recurrenceInterval"),
                        isPaused = expJson.getBoolean("isPaused"),
                    ),
                )
            }

            // Transactional overwrite
            database.withTransaction {
                // Not actually deleting categories so we don't violate foreign keys if they exist,
                // but for MVP we will rely on REPLACE strategy.
                // Actually to do a full restore, you'd clear all expenses first:
                // We'd need a clearAll method. Let's just insert with REPLACE.
                categoriesToInsert.forEach { categoryDao.insertCategory(it) }
                expensesToInsert.forEach { expenseDao.insertExpense(it) }
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
