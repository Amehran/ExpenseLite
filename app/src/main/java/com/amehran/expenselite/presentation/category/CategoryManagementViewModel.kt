package com.amehran.expenselite.presentation.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amehran.expenselite.domain.model.Category
import com.amehran.expenselite.domain.repository.TransactionRepository
import com.amehran.expenselite.domain.usecase.DeleteCategoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryManagementViewModel @Inject constructor(
    private val repository: TransactionRepository,
    private val deleteCategoryUseCase: DeleteCategoryUseCase,
) : ViewModel() {

    val categories: StateFlow<List<Category>> = repository.getAllCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _uiState = MutableStateFlow(CategoryManagementState())
    val uiState: StateFlow<CategoryManagementState> = _uiState.asStateFlow()

    fun onEvent(event: CategoryManagementEvent) {
        when (event) {
            is CategoryManagementEvent.UpdateNewCategoryName -> {
                _uiState.value = _uiState.value.copy(newCategoryName = event.name)
            }
            is CategoryManagementEvent.AddCategory -> {
                addCategory()
            }
            is CategoryManagementEvent.DeleteCategory -> {
                deleteCategory(event.category)
            }
            is CategoryManagementEvent.DismissError -> {
                _uiState.value = _uiState.value.copy(errorMessage = null)
            }
        }
    }

    private fun addCategory() {
        val name = _uiState.value.newCategoryName.trim()
        if (name.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Category name cannot be empty")
            return
        }

        viewModelScope.launch {
            try {
                val newCategory = Category(
                    id = 0,
                    name = name,
                    iconResName = "ic_custom",
                    isSystemDefault = false,
                )
                repository.addCategory(newCategory)
                _uiState.value = _uiState.value.copy(newCategoryName = "", errorMessage = null)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = "Failed to add category: ${e.message}")
            }
        }
    }

    private fun deleteCategory(category: Category) {
        viewModelScope.launch {
            deleteCategoryUseCase(category).fold(
                onSuccess = {
                    // Refresh handled automatically by Flow
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(errorMessage = error.message)
                },
            )
        }
    }
}

data class CategoryManagementState(
    val newCategoryName: String = "",
    val errorMessage: String? = null,
)

sealed interface CategoryManagementEvent {
    data class UpdateNewCategoryName(val name: String) : CategoryManagementEvent
    data object AddCategory : CategoryManagementEvent
    data class DeleteCategory(val category: Category) : CategoryManagementEvent
    data object DismissError : CategoryManagementEvent
}
