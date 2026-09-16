package com.amehran.expenselite.domain.usecase

import androidx.room.withTransaction
import com.amehran.expenselite.data.local.ExpenseDatabase
import com.amehran.expenselite.data.local.dao.CategoryDao
import com.amehran.expenselite.data.local.dao.ExpenseDao
import com.amehran.expenselite.data.local.entity.CategoryEntity
import com.amehran.expenselite.data.local.entity.ExpenseEntity
import org.json.JSONArray
import org.json.JSONObject
import java.io.InputStream
import javax.inject.Inject

enum class ConflictStrategy {
    MERGE,
    OVERWRITE,
}

open class ImportDataUseCase @Inject constructor(
    private val database: ExpenseDatabase?,
    private val categoryDao: CategoryDao?,
    private val expenseDao: ExpenseDao?,
) {
    @Suppress("TooGenericExceptionCaught")
    open suspend operator fun invoke(
        inputStream: InputStream,
        conflictStrategy: ConflictStrategy = ConflictStrategy.MERGE,
    ): Result<Unit> {
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

            val categoriesToInsert = parseCategories(categoriesArray)
            val expensesToInsert = parseExpenses(expensesArray)

            performImportTransaction(conflictStrategy, categoriesToInsert, expensesToInsert)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun parseCategories(categoriesArray: JSONArray): List<CategoryEntity> {
        val list = mutableListOf<CategoryEntity>()
        for (i in 0 until categoriesArray.length()) {
            val catJson = categoriesArray.getJSONObject(i)
            list.add(
                CategoryEntity(
                    id = catJson.getLong("id"),
                    name = catJson.getString("name"),
                    iconResName = catJson.getString("iconResName"),
                    isSystemDefault = catJson.getBoolean("isSystemDefault"),
                    colorHex = catJson.optString("colorHex", ""),
                ),
            )
        }
        return list
    }

    private fun parseExpenses(expensesArray: JSONArray): List<ExpenseEntity> {
        val list = mutableListOf<ExpenseEntity>()
        for (i in 0 until expensesArray.length()) {
            val expJson = expensesArray.getJSONObject(i)
            list.add(
                ExpenseEntity(
                    id = expJson.getLong("id"),
                    title = expJson.getString("title"),
                    amountCents = expJson.getLong("amountCents"),
                    categoryId = expJson.getLong("categoryId"),
                    categoryName = expJson.optString("categoryName", ""),
                    categoryColorHex = expJson.optString("categoryColorHex", ""),
                    timestamp = expJson.getLong("timestamp"),
                    isIncome = expJson.getBoolean("isIncome"),
                    isSubscription = expJson.getBoolean("isSubscription"),
                    recurrenceInterval = expJson.getString("recurrenceInterval"),
                    isPaused = expJson.getBoolean("isPaused"),
                ),
            )
        }
        return list
    }

    private suspend fun performImportTransaction(
        conflictStrategy: ConflictStrategy,
        categoriesToInsert: List<CategoryEntity>,
        expensesToInsert: List<ExpenseEntity>,
    ) {
        if (database != null) {
            database.withTransaction {
                executeInsertions(conflictStrategy, categoriesToInsert, expensesToInsert)
            }
        } else if (categoryDao != null && expenseDao != null) {
            executeInsertions(conflictStrategy, categoriesToInsert, expensesToInsert)
        }
    }

    private suspend fun executeInsertions(
        conflictStrategy: ConflictStrategy,
        categoriesToInsert: List<CategoryEntity>,
        expensesToInsert: List<ExpenseEntity>,
    ) {
        if (conflictStrategy == ConflictStrategy.OVERWRITE) {
            expenseDao?.deleteAllExpenses()
            categoryDao?.deleteNonSystemCategories()
        }

        categoriesToInsert.forEach { categoryDao?.insertCategory(it) }
        expensesToInsert.forEach { expenseDao?.insertExpense(it) }
    }
}
