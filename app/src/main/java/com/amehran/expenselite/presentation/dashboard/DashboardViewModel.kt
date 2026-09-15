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

    val uiState: StateFlow<DashboardState> =
        combine(
            _monthOffset.flatMapLatest { offset ->
                val (start, end) = getMonthDateRange(offset)
                getDashboardDataUseCase(startTimestamp = start, endTimestamp = end)
            },
            calculateProjectedSubscriptionsUseCase(),
            _monthOffset,
        ) { data, projectedSubscriptionsCents, offset ->
            DashboardState.Success(
                selectedMonthLabel = getMonthLabel(offset),
                monthOffset = offset,
                totalBalance = formatCurrency(data.totalBalanceCents),
                totalIncome = formatCurrency(data.totalIncomeCents),
                totalExpense = formatCurrency(data.totalExpenseCents),
                projectedSubscriptions = formatCurrency(projectedSubscriptionsCents),
                recentTransactions = data.recentTransactions,
            )
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
        val recentTransactions: List<Expense>,
    ) : DashboardState
}
