package com.amehran.expenselite.domain.usecase

import com.amehran.expenselite.domain.model.Expense
import com.amehran.expenselite.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

data class DashboardData(
    val totalBalanceCents: Long,
    val totalIncomeCents: Long,
    val totalExpenseCents: Long,
    val recentTransactions: List<Expense>
)

class GetDashboardDataUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    operator fun invoke(): Flow<DashboardData> {
        return repository.getAllExpenses().map { expenses ->
            var income = 0L
            var expense = 0L
            expenses.forEach {
                if (it.isIncome) {
                    income += it.amountCents
                } else {
                    expense += it.amountCents
                }
            }
            
            DashboardData(
                totalBalanceCents = income - expense,
                totalIncomeCents = income,
                totalExpenseCents = expense,
                recentTransactions = expenses.take(10) // Show last 10
            )
        }
    }
}
