package com.amehran.expenselite.presentation.dashboard

import com.amehran.expenselite.domain.model.Category
import com.amehran.expenselite.domain.model.Expense
import com.amehran.expenselite.domain.model.RecurrenceInterval
import com.amehran.expenselite.domain.repository.TransactionRepository
import com.amehran.expenselite.domain.usecase.CalculateProjectedSubscriptionsUseCase
import com.amehran.expenselite.domain.usecase.GetDashboardDataUseCase
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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeRepository: FakeTransactionRepository
    private lateinit var getDashboardDataUseCase: GetDashboardDataUseCase
    private lateinit var calculateProjectedSubscriptionsUseCase: CalculateProjectedSubscriptionsUseCase
    private lateinit var viewModel: DashboardViewModel

    private val sampleExpense1 = Expense(
        id = 1,
        title = "Groceries",
        amountCents = 5000,
        categoryId = 101,
        categoryName = "Food",
        timestamp = 1000L,
        isIncome = false,
        isSubscription = false,
        recurrenceInterval = RecurrenceInterval.NONE,
        isPaused = false,
    )

    private val sampleIncome = Expense(
        id = 2,
        title = "Salary",
        amountCents = 300000,
        categoryId = 102,
        categoryName = "Income",
        timestamp = 2000L,
        isIncome = true,
        isSubscription = false,
        recurrenceInterval = RecurrenceInterval.NONE,
        isPaused = false,
    )

    private val sampleSubscription = Expense(
        id = 3,
        title = "Netflix",
        amountCents = 1500,
        categoryId = 103,
        categoryName = "Entertainment",
        timestamp = 3000L,
        isIncome = false,
        isSubscription = true,
        recurrenceInterval = RecurrenceInterval.MONTHLY,
        isPaused = false,
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeTransactionRepository()
        fakeRepository.expenses = listOf(sampleExpense1, sampleIncome, sampleSubscription)
        fakeRepository.filteredExpenses = listOf(sampleExpense1, sampleIncome, sampleSubscription)

        getDashboardDataUseCase = GetDashboardDataUseCase(fakeRepository)
        calculateProjectedSubscriptionsUseCase = CalculateProjectedSubscriptionsUseCase(fakeRepository)

        viewModel = DashboardViewModel(
            getDashboardDataUseCase = getDashboardDataUseCase,
            calculateProjectedSubscriptionsUseCase = calculateProjectedSubscriptionsUseCase,
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial uiState computes dashboard metrics and filters correctly`() = runTest(testDispatcher) {
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.first { it is DashboardState.Success } as DashboardState.Success
        assertEquals(0, state.monthOffset)
        assertEquals("$2935.00", state.totalBalance)
        assertEquals("$3000.00", state.totalIncome)
        assertEquals("$65.00", state.totalExpense)
        assertEquals("$15.00", state.projectedSubscriptions)
        assertEquals(3, state.recentTransactions.size)
        assertEquals(TransactionFilter.All, state.activeFilter)
        assertFalse(state.isSearchActive)
        assertEquals("", state.searchQuery)
    }

    @Test
    fun `month navigation updates monthOffset`() = runTest(testDispatcher) {
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.selectPreviousMonth()
        assertEquals(-1, viewModel.monthOffset.value)

        viewModel.selectNextMonth()
        assertEquals(0, viewModel.monthOffset.value)
    }

    @Test
    fun `filtering by Income returns only income transactions`() = runTest(testDispatcher) {
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.setFilter(TransactionFilter.Income)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.first { it is DashboardState.Success } as DashboardState.Success
        assertEquals(TransactionFilter.Income, state.activeFilter)
        assertEquals(1, state.recentTransactions.size)
        assertEquals("Salary", state.recentTransactions.first().title)
    }

    @Test
    fun `filtering by Expense returns only expense transactions`() = runTest(testDispatcher) {
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.setFilter(TransactionFilter.Expense)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.first { it is DashboardState.Success } as DashboardState.Success
        assertEquals(101, (state.recentTransactions.first()).categoryId)
        assertEquals(2, state.recentTransactions.size)
    }

    @Test
    fun `filtering by specific Category returns matching category transactions`() = runTest(testDispatcher) {
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.setFilter(TransactionFilter.Category(101, "Food"))
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.first { it is DashboardState.Success } as DashboardState.Success
        assertEquals(1, state.recentTransactions.size)
        assertEquals("Groceries", state.recentTransactions.first().title)
    }

    @Test
    fun `toggleSearch and setSearchQuery updates state and filters transactions`() = runTest(testDispatcher) {
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.toggleSearch()
        assertTrue(viewModel.isSearchActive.value)

        viewModel.setSearchQuery("Netfl")
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.first { it is DashboardState.Success } as DashboardState.Success
        assertTrue(state.isSearchActive)
        assertEquals("Netfl", state.searchQuery)
        assertEquals(1, state.recentTransactions.size)
        assertEquals("Netflix", state.recentTransactions.first().title)

        viewModel.toggleSearch()
        assertFalse(viewModel.isSearchActive.value)
        assertEquals("", viewModel.searchQuery.value)
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
