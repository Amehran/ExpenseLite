package com.amehran.expenselite.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val amountCents: Long,
    val categoryId: Long,
    val timestamp: Long,
    val isIncome: Boolean,
    val isSubscription: Boolean,
    val recurrenceInterval: String, // "NONE", "MONTHLY", "YEARLY"
    val isPaused: Boolean,
)
