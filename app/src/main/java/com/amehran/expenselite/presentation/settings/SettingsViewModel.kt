package com.amehran.expenselite.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amehran.expenselite.domain.usecase.ExportDataUseCase
import com.amehran.expenselite.domain.usecase.ImportDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val exportDataUseCase: ExportDataUseCase,
    private val importDataUseCase: ImportDataUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsState())
    val uiState: StateFlow<SettingsState> = _uiState.asStateFlow()

    fun exportData(outputStream: OutputStream) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, message = null)
            exportDataUseCase(outputStream).fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(isLoading = false, message = "Export successful")
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(isLoading = false, message = "Export failed: ${error.message}")
                },
            )
        }
    }

    fun importData(inputStream: InputStream) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, message = null)
            importDataUseCase(inputStream).fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(isLoading = false, message = "Import successful")
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(isLoading = false, message = "Import failed: ${error.message}")
                },
            )
        }
    }

    fun dismissMessage() {
        _uiState.value = _uiState.value.copy(message = null)
    }
}

data class SettingsState(
    val isLoading: Boolean = false,
    val message: String? = null,
)
