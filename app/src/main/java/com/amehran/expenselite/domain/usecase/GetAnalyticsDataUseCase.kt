package com.amehran.expenselite.domain.usecase

import com.amehran.expenselite.domain.model.Category
import com.amehran.expenselite.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.util.Calendar
import javax.inject.Inject

data class CategorySpend(
    val category: Category,
    val amountCents: Long,
    val percentage: Float,
)

class GetAnalyticsDataUseCase @Inject constructor(
    private val repository: TransactionRepository,
) {
    operator fun invoke(year: Int, month: Int): Flow<List<CategorySpend>> {
        return combine(
            repository.getAllExpenses(),
            repository.getAllCategories(),
        ) { expenses, categories ->
            val filteredExpenses = expenses.filter { expense ->
                !expense.isIncome && isSameMonthAndYear(expense.timestamp, year, month)
            }

            val totalSpend = filteredExpenses.sumOf { it.amountCents }

            if (totalSpend == 0L) return@combine emptyList()

            val spendByCategory = filteredExpenses
                .groupBy { it.categoryId }
                .mapValues { entry -> entry.value.sumOf { it.amountCents } }

            spendByCategory.mapNotNull { (categoryId, amount) ->
                val category = categories.find { it.id == categoryId }
                if (category != null) {
                    CategorySpend(
                        category = category,
                        amountCents = amount,
                        percentage = (amount.toFloat() / totalSpend.toFloat()) * 100f,
                    )
                } else {
                    null
                }
            }.sortedByDescending { it.amountCents }
        }
    }

    private fun isSameMonthAndYear(timestamp: Long, year: Int, month: Int): Boolean {
        val calendar = Calendar.getInstance().apply { timeInMillis = timestamp }
        return calendar.get(Calendar.YEAR) == year && calendar.get(Calendar.MONTH) == month
    }
}
