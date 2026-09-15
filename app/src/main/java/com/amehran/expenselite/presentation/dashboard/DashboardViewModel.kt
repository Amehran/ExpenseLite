package com.amehran.expenselite.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amehran.expenselite.domain.model.Expense
import com.amehran.expenselite.domain.usecase.CalculateProjectedSubscriptionsUseCase
import com.amehran.expenselite.domain.usecase.GetDashboardDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel
@Inject
constructor(
    getDashboardDataUseCase: GetDashboardDataUseCase,
    calculateProjectedSubscriptionsUseCase: CalculateProjectedSubscriptionsUseCase,
) : ViewModel() {
    val uiState: StateFlow<DashboardState> =
        combine(
            getDashboardDataUseCase(),
            calculateProjectedSubscriptionsUseCase(),
        ) { data, projectedSubscriptionsCents ->
            DashboardState.Success(
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

    private fun formatCurrency(cents: Long): String {
        val dollars = cents / 100.0
        return String.format("$%.2f", dollars)
    }
}

sealed interface DashboardState {
    data object Loading : DashboardState

    data class Success(
        val totalBalance: String,
        val totalIncome: String,
        val totalExpense: String,
        val projectedSubscriptions: String,
        val recentTransactions: List<Expense>,
    ) : DashboardState
}
