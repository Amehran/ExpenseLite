package com.amehran.expenselite.domain.repository

import com.amehran.expenselite.domain.model.Category
import com.amehran.expenselite.domain.model.Expense
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    fun getAllExpenses(): Flow<List<Expense>>
    fun getAllCategories(): Flow<List<Category>>
    suspend fun getExpenseById(id: Long): Expense?
    suspend fun getCategoryById(id: Long): Category?
    suspend fun addExpense(expense: Expense): Long
    suspend fun updateExpense(expense: Expense)
    suspend fun deleteExpense(expense: Expense)
}
