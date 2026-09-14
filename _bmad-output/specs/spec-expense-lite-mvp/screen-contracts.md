# screen-contracts.md — UI Screen State Contracts
_Companion to SPEC.md (spec-expense-lite-mvp). Read alongside SPEC.md for the full contract._

Every feature screen defines three sealed interfaces. ViewModels own `UiState` and `SideEffect`; the UI layer sends `UiEvent` to the ViewModel. This is the full machine contract; deviations are a spec violation.

---

## Screen 1: Dashboard (Home)

### DashboardUiState

```kotlin
sealed interface DashboardUiState {
    data object Loading : DashboardUiState
    data class Success(
        val summary: MonthlySummary,
        val recentExpenses: List<Expense>,
        val activeFilter: CategoryFilter,
    ) : DashboardUiState
    data class Error(val message: String) : DashboardUiState
}
```

**Loading:** Renders skeleton loaders for summary cards and the recent transactions list.
**Empty:** When `Success.recentExpenses` is empty — renders a centered illustration with "No transactions found for this period" and a prominent "Add Transaction" CTA button.

### DashboardUiEvent

```kotlin
sealed interface DashboardUiEvent {
    data object OnAddExpenseClicked : DashboardUiEvent
    data class OnFilterCategory(val categoryId: String?) : DashboardUiEvent
    data class OnDateRangeSelected(val startDate: Long, val endDate: Long) : DashboardUiEvent
    data class OnDeleteExpense(val expenseId: String) : DashboardUiEvent
    data class OnToggleSubscriptionPause(val subscriptionId: String) : DashboardUiEvent
}
```

### DashboardSideEffect

```kotlin
sealed interface DashboardSideEffect {
    data class NavigateToAddEdit(val expenseId: String?) : DashboardSideEffect
    data class ShowSnackbar(val message: String) : DashboardSideEffect
}
```

---

## Screen 2: Analytics & Reports

### AnalyticsUiState

```kotlin
sealed interface AnalyticsUiState {
    data object Loading : AnalyticsUiState
    data class Success(
        val categoryTotals: List<CategorySpend>,
        val totalSpend: Long,
    ) : AnalyticsUiState
    data class Error(val message: String) : AnalyticsUiState
}
```

### AnalyticsUiEvent

```kotlin
sealed interface AnalyticsUiEvent {
    data class OnMonthChanged(val monthOffset: Int) : AnalyticsUiEvent
}
```

`monthOffset = 0` means the current month. Negative values go back in time.

### AnalyticsSideEffect

```kotlin
sealed interface AnalyticsSideEffect {
    data class ShowSnackbar(val message: String) : AnalyticsSideEffect
}
```

---

## Screen 3: Add / Edit Transaction

### AddEditUiState

```kotlin
sealed interface AddEditUiState {
    data object Loading : AddEditUiState
    data class Success(
        val title: String,
        val amountInput: String,   // raw user input string before parsing
        val selectedCategory: Category?,
        val selectedDate: Long,
        val isIncome: Boolean,
        val isSubscription: Boolean,
        val recurrenceInterval: RecurrenceInterval,
        val isPaused: Boolean,
        val availableCategories: List<Category>,
        val isEditMode: Boolean,
        val titleError: String?,
        val amountError: String?,
    ) : AddEditUiState
    data class Error(val message: String) : AddEditUiState
}
```

### AddEditUiEvent

```kotlin
sealed interface AddEditUiEvent {
    data class OnTitleChanged(val value: String) : AddEditUiEvent
    data class OnAmountChanged(val value: String) : AddEditUiEvent
    data class OnCategorySelected(val categoryId: String) : AddEditUiEvent
    data class OnDateSelected(val epochMs: Long) : AddEditUiEvent
    data class OnIsIncomeToggled(val value: Boolean) : AddEditUiEvent
    data class OnIsSubscriptionToggled(val value: Boolean) : AddEditUiEvent
    data class OnRecurrenceIntervalChanged(val interval: RecurrenceInterval) : AddEditUiEvent
    data class OnIsPausedToggled(val value: Boolean) : AddEditUiEvent
    data object OnSaveClicked : AddEditUiEvent
    data object OnDeleteClicked : AddEditUiEvent
    data object OnNavigateBack : AddEditUiEvent
}
```

### AddEditSideEffect

```kotlin
sealed interface AddEditSideEffect {
    data object NavigateBack : AddEditSideEffect
    data class ShowSnackbar(val message: String) : AddEditSideEffect
}
```

---

## Screen 4: Settings & Data Management

### SettingsUiState

```kotlin
sealed interface SettingsUiState {
    // Settings screen is always ready; no Loading state needed
    data class Success(
        val isDarkMode: Boolean,
        val lastBackupTimestamp: Long?,
    ) : SettingsUiState
}
```

### SettingsUiEvent

```kotlin
sealed interface SettingsUiEvent {
    data class OnToggleDarkMode(val enabled: Boolean) : SettingsUiEvent
    data object OnExportDataClicked : SettingsUiEvent
    data object OnImportDataClicked : SettingsUiEvent
}
```

### SettingsSideEffect

```kotlin
sealed interface SettingsSideEffect {
    data object LaunchFilePickerForExport : SettingsSideEffect
    data object LaunchFilePickerForImport : SettingsSideEffect
    data class ShowSnackbar(val message: String) : SettingsSideEffect
}
```

---

## Screen 5: Category Management

### CategoryUiState

```kotlin
sealed interface CategoryUiState {
    data object Loading : CategoryUiState
    data class Success(
        val systemCategories: List<Category>,
        val customCategories: List<Category>,
    ) : CategoryUiState
    data class Error(val message: String) : CategoryUiState
}
```

### CategoryUiEvent

```kotlin
sealed interface CategoryUiEvent {
    data class OnAddCategory(val name: String, val iconResName: String) : CategoryUiEvent
    data class OnDeleteCategoryRequested(val categoryId: String) : CategoryUiEvent
    data class OnReassignAndDelete(val categoryId: String, val targetCategoryId: String) : CategoryUiEvent
}
```

### CategorySideEffect

```kotlin
sealed interface CategorySideEffect {
    data class ShowReassignDialog(val categoryId: String, val affectedCount: Int) : CategorySideEffect
    data class ShowSnackbar(val message: String) : CategorySideEffect
}
```

---

## Navigation Routes

All routes are `@Serializable`. Defined as top-level objects/classes in a `NavigationRoute` sealed hierarchy:

```kotlin
@Serializable object DashboardRoute
@Serializable object AnalyticsRoute
@Serializable data class AddEditRoute(val expenseId: String? = null)
@Serializable object SettingsRoute
@Serializable object CategoryRoute
```

Bottom navigation tabs: Dashboard, Analytics, Settings. Category management accessed from Settings. Add/Edit accessed from Dashboard (FAB) and item long-press.
