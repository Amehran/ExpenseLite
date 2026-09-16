package com.amehran.expenselite.domain.usecase

import com.amehran.expenselite.data.local.dao.CategoryDao
import com.amehran.expenselite.data.local.dao.ExpenseDao
import kotlinx.coroutines.flow.first
import org.json.JSONArray
import org.json.JSONObject
import java.io.OutputStream
import javax.inject.Inject

class ExportDataUseCase @Inject constructor(
    private val categoryDao: CategoryDao,
    private val expenseDao: ExpenseDao,
) {
    suspend operator fun invoke(outputStream: OutputStream): Result<Unit> {
        return try {
            val categories = categoryDao.getAllCategories().first()
            val expenses = expenseDao.getAllExpenses().first()

            val rootJson = JSONObject()

            // Serialize Categories
            val categoriesArray = JSONArray()
            categories.forEach { category ->
                val catJson = JSONObject().apply {
                    put("id", category.id)
                    put("name", category.name)
                    put("iconResName", category.iconResName)
                    put("isSystemDefault", category.isSystemDefault)
                }
                categoriesArray.put(catJson)
            }
            rootJson.put("categories", categoriesArray)

            // Serialize Expenses
            val expensesArray = JSONArray()
            expenses.forEach { expense ->
                val expJson = JSONObject().apply {
                    put("id", expense.id)
                    put("title", expense.title)
                    put("amountCents", expense.amountCents)
                    put("categoryId", expense.categoryId)
                    put("timestamp", expense.timestamp)
                    put("isIncome", expense.isIncome)
                    put("isSubscription", expense.isSubscription)
                    put("recurrenceInterval", expense.recurrenceInterval)
                    put("isPaused", expense.isPaused)
                }
                expensesArray.put(expJson)
            }
            rootJson.put("expenses", expensesArray)

            // Write to output stream
            outputStream.use { stream ->
                stream.write(rootJson.toString(2).toByteArray(Charsets.UTF_8))
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
