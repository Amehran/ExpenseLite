package com.amehran.expenselite.domain.usecase

import com.amehran.expenselite.data.local.dao.CategoryDao
import com.amehran.expenselite.data.local.dao.ExpenseDao
import com.amehran.expenselite.data.local.entity.CategoryEntity
import com.amehran.expenselite.data.local.entity.ExpenseEntity
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Test
import java.io.ByteArrayInputStream

class ImportDataUseCaseTest {

    @Test
    fun `importData parses JSON backup file successfully`() = runBlocking {
        val jsonBackup = """
            {
              "schemaVersion": 2,
              "categories": [
                {
                  "id": 10,
                  "name": "Shopping",
                  "iconResName": "ic_shopping",
                  "isSystemDefault": false,
                  "colorHex": "#FF9800"
                }
              ],
              "expenses": [
                {
                  "id": 200,
                  "title": "Shoes",
                  "amountCents": 8000,
                  "categoryId": 10,
                  "categoryName": "Shopping",
                  "categoryColorHex": "#FF9800",
                  "timestamp": 1600000000000,
                  "isIncome": false,
                  "isSubscription": false,
                  "recurrenceInterval": "NONE",
                  "isPaused": false
                }
              ]
            }
        """.trimIndent()

        val insertedCategories = mutableListOf<CategoryEntity>()
        val insertedExpenses = mutableListOf<ExpenseEntity>()
        var overwriteCleared = false

        val fakeCategoryDao = object : CategoryDao {
            override fun getAllCategories(): Flow<List<CategoryEntity>> = flowOf(emptyList())
            override suspend fun insertCategory(category: CategoryEntity): Long {
                insertedCategories.add(category)
                return category.id
            }
            override suspend fun updateCategory(category: CategoryEntity) {
                // Unused in tests
            }
            override suspend fun deleteCategory(category: CategoryEntity) {
                // Unused in tests
            }
            override suspend fun getCategoryById(id: Long): CategoryEntity? = null
            override suspend fun deleteNonSystemCategories() {
                overwriteCleared = true
            }
        }

        val fakeExpenseDao = object : ExpenseDao {
            override fun getAllExpenses(): Flow<List<ExpenseEntity>> = flowOf(emptyList())
            override fun getExpensesByDateRange(startTimestamp: Long, endTimestamp: Long): Flow<List<ExpenseEntity>> =
                flowOf(emptyList())
            override suspend fun insertExpense(expense: ExpenseEntity): Long {
                insertedExpenses.add(expense)
                return expense.id
            }
            override suspend fun updateExpense(expense: ExpenseEntity) {
                // Unused in tests
            }
            override suspend fun deleteExpense(expense: ExpenseEntity) {
                // Unused in tests
            }
            override suspend fun getExpenseById(id: Long): ExpenseEntity? = null
            override suspend fun reassignExpensesToUncategorized(oldCategoryId: Long) {
                // Unused in tests
            }
            override suspend fun deleteAllExpenses() {
                overwriteCleared = true
            }
        }

        val useCase = ImportDataUseCase(null, fakeCategoryDao, fakeExpenseDao)
        val inputStream = ByteArrayInputStream(jsonBackup.toByteArray(Charsets.UTF_8))

        val result = useCase(inputStream, ConflictStrategy.MERGE)

        assertTrue(result.isSuccess)
        assertEquals(1, insertedCategories.size)
        assertEquals("Shopping", insertedCategories.first().name)
        assertEquals("#FF9800", insertedCategories.first().colorHex)
        assertEquals(1, insertedExpenses.size)
        assertEquals("Shoes", insertedExpenses.first().title)
    }
}
