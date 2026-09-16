package com.amehran.expenselite.domain.usecase

import com.amehran.expenselite.domain.model.RecurrenceInterval
import com.amehran.expenselite.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CalculateProjectedSubscriptionsUseCase @Inject constructor(
    private val repository: TransactionRepository,
) {
    /**
     * Returns the projected monthly cost of all active subscriptions in cents.
     */
    operator fun invoke(): Flow<Long> {
        return repository.getAllExpenses().map { expenses ->
            var projectedMonthlyTotalCents = 0L

            expenses.filter { it.isSubscription && !it.isPaused }.forEach { expense ->
                when (expense.recurrenceInterval) {
                    RecurrenceInterval.MONTHLY -> {
                        projectedMonthlyTotalCents += expense.amountCents
                    }
                    RecurrenceInterval.YEARLY -> {
                        // Spread yearly over 12 months for monthly projection
                        projectedMonthlyTotalCents += (expense.amountCents / 12)
                    }
                    RecurrenceInterval.NONE -> {
                        // Should not happen for subscriptions, but ignore
                    }
                }
            }

            projectedMonthlyTotalCents
        }
    }
}
