package com.amehran.expenselite

sealed interface HomeUiState {
    data object Loading : HomeUiState

    data class Success(val items: List<String>) : HomeUiState

    data class Error(val message: String) : HomeUiState
}
