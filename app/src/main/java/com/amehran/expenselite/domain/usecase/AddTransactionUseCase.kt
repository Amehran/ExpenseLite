package com.amehran.expenselite.domain.usecase

import com.amehran.expenselite.domain.model.Expense
import com.amehran.expenselite.domain.repository.TransactionRepository
import javax.inject.Inject

class AddTransactionUseCase
@Inject
constructor(
    private val repository: TransactionRepository,
) {
    suspend operator fun invoke(expense: Expense): Result<Long> {
        if (expense.amountCents <= 0) {
            return Result.failure(IllegalArgumentException("Amount must be greater than 0"))
        }
        if (expense.title.isBlank()) {
            return Result.failure(IllegalArgumentException("Title cannot be empty"))
        }

        return try {
            val id = repository.addExpense(expense)
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
