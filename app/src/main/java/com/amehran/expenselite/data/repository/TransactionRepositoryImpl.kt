package com.amehran.expenselite.data.repository

import com.amehran.expenselite.data.local.dao.CategoryDao
import com.amehran.expenselite.data.local.dao.ExpenseDao
import com.amehran.expenselite.data.local.entity.CategoryEntity
import com.amehran.expenselite.data.local.entity.ExpenseEntity
import com.amehran.expenselite.domain.model.Category
import com.amehran.expenselite.domain.model.Expense
import com.amehran.expenselite.domain.model.RecurrenceInterval
import com.amehran.expenselite.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TransactionRepositoryImpl
@Inject
constructor(
    private val expenseDao: ExpenseDao,
    private val categoryDao: CategoryDao,
) : TransactionRepository {
    override fun getAllExpenses(): Flow<List<Expense>> {
        return expenseDao.getAllExpenses().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getExpensesByDateRange(startTimestamp: Long, endTimestamp: Long): Flow<List<Expense>> {
        return expenseDao.getExpensesByDateRange(startTimestamp, endTimestamp).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getAllCategories(): Flow<List<Category>> {
        return categoryDao.getAllCategories().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun getExpenseById(id: Long): Expense? {
        return expenseDao.getExpenseById(id)?.toDomainModel()
    }

    override suspend fun getCategoryById(id: Long): Category? {
        return categoryDao.getCategoryById(id)?.toDomainModel()
    }

    override suspend fun addExpense(expense: Expense): Long {
        return expenseDao.insertExpense(expense.toEntity())
    }

    override suspend fun updateExpense(expense: Expense) {
        expenseDao.updateExpense(expense.toEntity())
    }

    override suspend fun deleteExpense(expense: Expense) {
        expenseDao.deleteExpense(expense.toEntity())
    }

    override suspend fun addCategory(category: Category): Long {
        return categoryDao.insertCategory(category.toEntity())
    }

    override suspend fun deleteCategory(category: Category) {
        categoryDao.deleteCategory(category.toEntity())
    }

    override suspend fun reassignExpensesToUncategorized(oldCategoryId: Long) {
        expenseDao.reassignExpensesToUncategorized(oldCategoryId)
    }

    // Mappers
    private fun ExpenseEntity.toDomainModel(): Expense {
        return Expense(
            id = this.id,
            title = this.title,
            amountCents = this.amountCents,
            categoryId = this.categoryId,
            categoryName = this.categoryName,
            categoryColorHex = this.categoryColorHex,
            timestamp = this.timestamp,
            isIncome = this.isIncome,
            isSubscription = this.isSubscription,
            recurrenceInterval = RecurrenceInterval.valueOf(this.recurrenceInterval),
            isPaused = this.isPaused,
        )
    }

    private fun Expense.toEntity(): ExpenseEntity {
        return ExpenseEntity(
            id = this.id,
            title = this.title,
            amountCents = this.amountCents,
            categoryId = this.categoryId,
            categoryName = this.categoryName,
            categoryColorHex = this.categoryColorHex,
            timestamp = this.timestamp,
            isIncome = this.isIncome,
            isSubscription = this.isSubscription,
            recurrenceInterval = this.recurrenceInterval.name,
            isPaused = this.isPaused,
        )
    }

    private fun CategoryEntity.toDomainModel(): Category {
        return Category(
            id = this.id,
            name = this.name,
            iconResName = this.iconResName,
            isSystemDefault = this.isSystemDefault,
            colorHex = this.colorHex,
        )
    }

    private fun Category.toEntity(): CategoryEntity {
        return CategoryEntity(
            id = this.id,
            name = this.name,
            iconResName = this.iconResName,
            isSystemDefault = this.isSystemDefault,
            colorHex = this.colorHex,
        )
    }
}
