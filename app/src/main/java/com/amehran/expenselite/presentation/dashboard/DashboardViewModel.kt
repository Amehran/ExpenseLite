package com.amehran.expenselite.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amehran.expenselite.domain.model.Expense
import com.amehran.expenselite.domain.usecase.CalculateProjectedSubscriptionsUseCase
import com.amehran.expenselite.domain.usecase.GetDashboardDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

sealed interface TransactionFilter {
    data object All : TransactionFilter
    data object Income : TransactionFilter
    data object Expense : TransactionFilter
    data class Category(val id: Long, val name: String) : TransactionFilter
}

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class DashboardViewModel
@Inject
constructor(
    private val getDashboardDataUseCase: GetDashboardDataUseCase,
    calculateProjectedSubscriptionsUseCase: CalculateProjectedSubscriptionsUseCase,
) : ViewModel() {

    private val _monthOffset = MutableStateFlow(0)
    val monthOffset: StateFlow<Int> = _monthOffset.asStateFlow()

    private val _activeFilter = MutableStateFlow<TransactionFilter>(TransactionFilter.All)
    val activeFilter: StateFlow<TransactionFilter> = _activeFilter.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isSearchActive = MutableStateFlow(false)
    val isSearchActive: StateFlow<Boolean> = _isSearchActive.asStateFlow()

    val uiState: StateFlow<DashboardState> =
        combine(
            _monthOffset.flatMapLatest { offset ->
                val (start, end) = getMonthDateRange(offset)
                getDashboardDataUseCase(startTimestamp = start, endTimestamp = end)
            },
            calculateProjectedSubscriptionsUseCase(),
            _monthOffset,
            _activeFilter,
            _searchQuery,
        ) { data, projectedSubscriptionsCents, offset, filter, query ->
            val daysInMonth = YearMonth.now().plusMonths(offset.toLong()).lengthOfMonth()
            val avgDailySpendCents = if (daysInMonth > 0) data.totalExpenseCents / daysInMonth else 0L

            val filteredTransactions = data.recentTransactions.filter { expense ->
                val matchesFilter = when (filter) {
                    is TransactionFilter.All -> true
                    is TransactionFilter.Income -> expense.isIncome
                    is TransactionFilter.Expense -> !expense.isIncome
                    is TransactionFilter.Category -> expense.categoryId == filter.id
                }
                val matchesSearch = query.isBlank() || expense.title.contains(query, ignoreCase = true)
                matchesFilter && matchesSearch
            }

            val dynamicCategories = data.recentTransactions
                .map { expense ->
                    val name = expense.categoryName.ifBlank { "Uncategorized" }
                    TransactionFilter.Category(expense.categoryId, name)
                }
                .distinctBy { it.id }
                .sortedBy { it.name }

            val availableFilters = listOf(
                TransactionFilter.All,
                TransactionFilter.Income,
                TransactionFilter.Expense,
            ) + dynamicCategories

            DashboardState.Success(
                selectedMonthLabel = getMonthLabel(offset),
                monthOffset = offset,
                totalBalance = formatCurrency(data.totalBalanceCents),
                totalIncome = formatCurrency(data.totalIncomeCents),
                totalExpense = formatCurrency(data.totalExpenseCents),
                projectedSubscriptions = formatCurrency(projectedSubscriptionsCents),
                monthlyBurnRate = formatCurrency(data.totalExpenseCents),
                avgDailySpend = formatCurrency(avgDailySpendCents),
                recentTransactions = filteredTransactions,
                activeFilter = filter,
                availableFilters = availableFilters,
            )
        }.combine(_isSearchActive) { state, isSearchActive ->
            state.copy(isSearchActive = isSearchActive, searchQuery = _searchQuery.value)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DashboardState.Loading,
        )

    fun selectPreviousMonth() {
        _monthOffset.value -= 1
    }

    fun selectNextMonth() {
        _monthOffset.value += 1
    }

    fun setFilter(filter: TransactionFilter) {
        _activeFilter.value = filter
    }

    fun toggleSearch() {
        _isSearchActive.value = !_isSearchActive.value
        if (!_isSearchActive.value) {
            _searchQuery.value = ""
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    private fun getMonthDateRange(offset: Int): Pair<Long, Long> {
        val yearMonth = YearMonth.now().plusMonths(offset.toLong())
        val startInstant = yearMonth.atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant()
        val endInstant = yearMonth.plusMonths(1).atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant()
        return Pair(startInstant.toEpochMilli(), endInstant.toEpochMilli())
    }

    private fun getMonthLabel(offset: Int): String {
        val yearMonth = YearMonth.now().plusMonths(offset.toLong())
        val formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault())
        return yearMonth.format(formatter)
    }

    private fun formatCurrency(cents: Long): String {
        val dollars = cents / 100.0
        return String.format(Locale.US, "$%.2f", dollars)
    }
}

sealed interface DashboardState {
    data object Loading : DashboardState

    data class Success(
        val selectedMonthLabel: String,
        val monthOffset: Int,
        val totalBalance: String,
        val totalIncome: String,
        val totalExpense: String,
        val projectedSubscriptions: String,
        val monthlyBurnRate: String,
        val avgDailySpend: String,
        val recentTransactions: List<Expense>,
        val activeFilter: TransactionFilter,
        val availableFilters: List<TransactionFilter>,
        val isSearchActive: Boolean = false,
        val searchQuery: String = "",
    ) : DashboardState
}
