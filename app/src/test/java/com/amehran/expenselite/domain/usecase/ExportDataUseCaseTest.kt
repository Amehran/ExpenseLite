package com.amehran.expenselite.domain.usecase

import com.amehran.expenselite.data.local.dao.CategoryDao
import com.amehran.expenselite.data.local.dao.ExpenseDao
import com.amehran.expenselite.data.local.entity.CategoryEntity
import com.amehran.expenselite.data.local.entity.ExpenseEntity
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import java.io.ByteArrayOutputStream
import kotlin.test.Test

class ExportDataUseCaseTest {

    @Test
    fun `export data serializes categories and expenses to json`() = runBlocking {
        // Arrange
        val fakeCategoryDao = object : CategoryDao {
            override fun getAllCategories(): Flow<List<CategoryEntity>> = flowOf(
                listOf(CategoryEntity(id = 1, name = "Food", iconResName = "food_ic", isSystemDefault = true)),
            )
            override suspend fun insertCategory(category: CategoryEntity): Long = 1L
            override suspend fun updateCategory(category: CategoryEntity) {}
            override suspend fun deleteCategory(category: CategoryEntity) {}
            override suspend fun getCategoryById(id: Long): CategoryEntity? = null
        }

        val fakeExpenseDao = object : ExpenseDao {
            override fun getAllExpenses(): Flow<List<ExpenseEntity>> = flowOf(
                listOf(
                    ExpenseEntity(
                        id = 1,
                        title = "Lunch",
                        amountCents = 1500,
                        categoryId = 1,
                        timestamp = 123456789L,
                        isIncome = false,
                        isSubscription = false,
                        recurrenceInterval = "NONE",
                        isPaused = false,
                    ),
                ),
            )
            override fun getExpensesByDateRange(startTimestamp: Long, endTimestamp: Long): Flow<List<ExpenseEntity>> =
                flowOf(emptyList())
            override suspend fun insertExpense(expense: ExpenseEntity): Long = 1L
            override suspend fun updateExpense(expense: ExpenseEntity) {}
            override suspend fun deleteExpense(expense: ExpenseEntity) {}
            override suspend fun getExpenseById(id: Long): ExpenseEntity? = null
            override suspend fun reassignExpensesToUncategorized(oldCategoryId: Long) {}
        }

        val useCase = ExportDataUseCase(fakeCategoryDao, fakeExpenseDao)
        val outputStream = ByteArrayOutputStream()

        // Act
        val result = useCase(outputStream)

        // Assert
        assertTrue(result.isSuccess)
        val jsonOutput = outputStream.toString("UTF-8")

        // Verify JSON string contains expected data
        assertTrue(jsonOutput.contains("\"name\": \"Food\""))
        assertTrue(jsonOutput.contains("\"title\": \"Lunch\""))
        assertTrue(jsonOutput.contains("\"amountCents\": 1500"))
    }
}
