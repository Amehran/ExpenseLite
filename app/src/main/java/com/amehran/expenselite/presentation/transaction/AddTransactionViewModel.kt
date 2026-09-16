package com.amehran.expenselite.presentation.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amehran.expenselite.domain.model.Category
import com.amehran.expenselite.domain.model.Expense
import com.amehran.expenselite.domain.model.RecurrenceInterval
import com.amehran.expenselite.domain.repository.TransactionRepository
import com.amehran.expenselite.domain.usecase.AddTransactionUseCase
import com.amehran.expenselite.presentation.util.SnackbarController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddTransactionViewModel
@Inject
constructor(
    private val addTransactionUseCase: AddTransactionUseCase,
    private val repository: TransactionRepository,
) : ViewModel() {
    val categories: StateFlow<List<Category>> =
        repository.getAllCategories()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _uiState = MutableStateFlow(AddTransactionState())
    val uiState: StateFlow<AddTransactionState> = _uiState.asStateFlow()

    fun onEvent(event: AddTransactionEvent) {
        when (event) {
            is AddTransactionEvent.OnTitleChanged -> _uiState.value = _uiState.value.copy(title = event.title)
            is AddTransactionEvent.OnAmountChanged -> _uiState.value = _uiState.value.copy(amountString = event.amount)
            is AddTransactionEvent.OnCategorySelected -> _uiState.value = _uiState.value.copy(selectedCategoryId = event.categoryId)
            is AddTransactionEvent.OnTypeChanged -> _uiState.value = _uiState.value.copy(isIncome = event.isIncome)
            is AddTransactionEvent.OnRecurrenceChanged -> _uiState.value = _uiState.value.copy(recurrence = event.recurrence)
            is AddTransactionEvent.SaveTransaction -> saveTransaction()
        }
    }

    private fun saveTransaction() {
        val state = _uiState.value
        val amountCents = (state.amountString.toDoubleOrNull()?.times(100))?.toLong() ?: 0L

        if (state.selectedCategoryId == null) {
            viewModelScope.launch {
                SnackbarController.showMessage("Please select a category")
            }
            return
        }

        val category = categories.value.find { it.id == state.selectedCategoryId }

        val expense =
            Expense(
                id = 0,
                title = state.title,
                amountCents = amountCents,
                categoryId = state.selectedCategoryId,
                categoryName = category?.name ?: "",
                categoryColorHex = category?.colorHex ?: "",
                timestamp = System.currentTimeMillis(),
                isIncome = state.isIncome,
                isSubscription = state.recurrence != RecurrenceInterval.NONE,
                recurrenceInterval = state.recurrence,
                isPaused = false,
            )

        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true)
            addTransactionUseCase(expense).fold(
                onSuccess = {
                    _uiState.value = state.copy(isLoading = false, isSaved = true)
                },
                onFailure = { error ->
                    _uiState.value = state.copy(isLoading = false)
                    SnackbarController.showMessage(error.message ?: "Unknown error")
                },
            )
        }
    }
}

data class AddTransactionState(
    val title: String = "",
    val amountString: String = "",
    val selectedCategoryId: Long? = null,
    val isIncome: Boolean = false,
    val recurrence: RecurrenceInterval = RecurrenceInterval.NONE,
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
)

sealed interface AddTransactionEvent {
    data class OnTitleChanged(val title: String) : AddTransactionEvent

    data class OnAmountChanged(val amount: String) : AddTransactionEvent

    data class OnCategorySelected(val categoryId: Long) : AddTransactionEvent

    data class OnTypeChanged(val isIncome: Boolean) : AddTransactionEvent

    data class OnRecurrenceChanged(val recurrence: RecurrenceInterval) : AddTransactionEvent

    data object SaveTransaction : AddTransactionEvent
}
