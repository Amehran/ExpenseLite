package com.amehran.expenselite.domain.usecase

import androidx.room.withTransaction
import com.amehran.expenselite.data.local.ExpenseDatabase
import com.amehran.expenselite.domain.model.Category
import com.amehran.expenselite.domain.repository.TransactionRepository
import javax.inject.Inject

class DeleteCategoryUseCase @Inject constructor(
    private val repository: TransactionRepository,
    private val database: ExpenseDatabase,
) {
    suspend operator fun invoke(category: Category): Result<Unit> {
        if (category.isSystemDefault) {
            return Result.failure(IllegalArgumentException("Cannot delete system categories"))
        }

        return try {
            database.withTransaction {
                // Reassign all expenses that used this category to Uncategorized (ID = 1)
                repository.reassignExpensesToUncategorized(oldCategoryId = category.id)
                // Delete the category itself
                repository.deleteCategory(category)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
