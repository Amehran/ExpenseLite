package com.amehran.expenselite.presentation.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amehran.expenselite.domain.model.Category
import com.amehran.expenselite.domain.repository.TransactionRepository
import com.amehran.expenselite.domain.usecase.DeleteCategoryUseCase
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
        }
    }

    private fun addCategory() {
        val name = _uiState.value.newCategoryName.trim()
        if (name.isBlank()) {
            viewModelScope.launch {
                SnackbarController.showMessage("Category name cannot be empty")
            }
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
                _uiState.value = _uiState.value.copy(newCategoryName = "")
                SnackbarController.showMessage("Category added successfully")
            } catch (e: Exception) {
                SnackbarController.showMessage("Failed to add category: ${e.message}")
            }
        }
    }

    private fun deleteCategory(category: Category) {
        viewModelScope.launch {
            deleteCategoryUseCase(category).fold(
                onSuccess = {
                    SnackbarController.showMessage("Category deleted")
                },
                onFailure = { error ->
                    SnackbarController.showMessage(error.message ?: "Failed to delete category")
                },
            )
        }
    }
}

data class CategoryManagementState(
    val newCategoryName: String = "",
)

sealed interface CategoryManagementEvent {
    data class UpdateNewCategoryName(val name: String) : CategoryManagementEvent
    data object AddCategory : CategoryManagementEvent
    data class DeleteCategory(val category: Category) : CategoryManagementEvent
}
