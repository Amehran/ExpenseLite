package com.amehran.expenselite.domain.usecase

import com.amehran.expenselite.domain.model.Category
import com.amehran.expenselite.domain.model.Expense
import com.amehran.expenselite.domain.model.RecurrenceInterval
import com.amehran.expenselite.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetDashboardDataUseCaseTest {

    private lateinit var fakeRepository: FakeTransactionRepository
    private lateinit var useCase: GetDashboardDataUseCase

    @Before
    fun setUp() {
        fakeRepository = FakeTransactionRepository()
        useCase = GetDashboardDataUseCase(fakeRepository)
    }

    @Test
    fun invokeWithDateRangeFiltersExpensesAccordingly() = runBlocking {
        val expense1 = Expense(
            id = 1,
            title = "January Coffee",
            amountCents = 500,
            categoryId = 1,
            timestamp = 1000L,
            isIncome = false,
            isSubscription = false,
            recurrenceInterval = RecurrenceInterval.NONE,
            isPaused = false,
        )
        val expense2 = Expense(
            id = 2,
            title = "February Salary",
            amountCents = 500000,
            categoryId = 1,
            timestamp = 5000L,
            isIncome = true,
            isSubscription = false,
            recurrenceInterval = RecurrenceInterval.NONE,
            isPaused = false,
        )
        fakeRepository.expenses = listOf(expense1, expense2)
        fakeRepository.filteredExpenses = listOf(expense1)

        val result = useCase(startTimestamp = 500L, endTimestamp = 2000L).first()

        assertEquals(500L, result.totalExpenseCents)
        assertEquals(0L, result.totalIncomeCents)
        assertEquals(-500L, result.totalBalanceCents)
        assertEquals(1, result.recentTransactions.size)
        assertEquals("January Coffee", result.recentTransactions[0].title)
    }

    private class FakeTransactionRepository : TransactionRepository {
        var expenses = listOf<Expense>()
        var filteredExpenses = listOf<Expense>()

        override fun getAllExpenses(): Flow<List<Expense>> = flowOf(expenses)

        override fun getExpensesByDateRange(startTimestamp: Long, endTimestamp: Long): Flow<List<Expense>> {
            return flowOf(filteredExpenses)
        }

        override fun getAllCategories(): Flow<List<Category>> = flowOf(emptyList())

        override suspend fun getExpenseById(id: Long): Expense? = null

        override suspend fun getCategoryById(id: Long): Category? = null

        override suspend fun addExpense(expense: Expense): Long = 1L

        override suspend fun updateExpense(expense: Expense) {}

        override suspend fun deleteExpense(expense: Expense) {}

        override suspend fun addCategory(category: Category): Long = 1L

        override suspend fun deleteCategory(category: Category) {}

        override suspend fun reassignExpensesToUncategorized(oldCategoryId: Long) {}
    }
}
