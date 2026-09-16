package com.amehran.expenselite.domain.model

data class Category(
    val id: Long,
    val name: String,
    val iconResName: String,
    val isSystemDefault: Boolean,
    val colorHex: String = "",
)

data class Expense(
    val id: Long,
    val title: String,
    val amountCents: Long,
    val categoryId: Long,
    val categoryName: String = "",
    val categoryColorHex: String = "",
    val timestamp: Long,
    val isIncome: Boolean,
    val isSubscription: Boolean,
    val recurrenceInterval: RecurrenceInterval,
    val isPaused: Boolean,
)

enum class RecurrenceInterval {
    NONE,
    MONTHLY,
    YEARLY,
}
