package com.amehran.expenselite.presentation.analytics

import com.amehran.expenselite.domain.model.Category
import com.amehran.expenselite.domain.model.Expense
import com.amehran.expenselite.domain.model.RecurrenceInterval
import com.amehran.expenselite.domain.repository.TransactionRepository
import com.amehran.expenselite.domain.usecase.GetAnalyticsDataUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AnalyticsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeRepository: FakeTransactionRepository
    private lateinit var getAnalyticsDataUseCase: GetAnalyticsDataUseCase
    private lateinit var viewModel: AnalyticsViewModel

    private val sampleCategory = Category(
        id = 1,
        name = "Food",
        iconResName = "ic_food",
        isSystemDefault = true,
        colorHex = "#4CAF50",
    )

    private val sampleExpense = Expense(
        id = 100,
        title = "Groceries",
        amountCents = 4000,
        categoryId = 1,
        categoryName = "Food",
        categoryColorHex = "#4CAF50",
        timestamp = System.currentTimeMillis(),
        isIncome = false,
        isSubscription = false,
        recurrenceInterval = RecurrenceInterval.NONE,
        isPaused = false,
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeTransactionRepository()
        fakeRepository.categories = listOf(sampleCategory)
        fakeRepository.expenses = listOf(sampleExpense)

        getAnalyticsDataUseCase = GetAnalyticsDataUseCase(fakeRepository)
        viewModel = AnalyticsViewModel(getAnalyticsDataUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state returns Success with current month offset 0`() = runTest(testDispatcher) {
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.first { it is AnalyticsState.Success } as AnalyticsState.Success
        assertEquals(0, state.monthOffset)
        assertEquals(1, state.categorySpends.size)
        assertEquals("Food", state.categorySpends.first().category.name)
        assertEquals("#4CAF50", state.categorySpends.first().category.colorHex)
    }

    @Test
    fun `navigation changes monthOffset correctly`() = runTest(testDispatcher) {
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.selectPreviousMonth()
        assertEquals(-1, viewModel.monthOffset.value)

        viewModel.selectNextMonth()
        assertEquals(0, viewModel.monthOffset.value)
    }

    private class FakeTransactionRepository : TransactionRepository {
        var expenses = listOf<Expense>()
        var categories = listOf<Category>()

        override fun getAllExpenses(): Flow<List<Expense>> = flowOf(expenses)

        override fun getExpensesByDateRange(startTimestamp: Long, endTimestamp: Long): Flow<List<Expense>> = flowOf(expenses)

        override fun getAllCategories(): Flow<List<Category>> = flowOf(categories)

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
