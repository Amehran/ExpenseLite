package com.amehran.expenselite.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amehran.expenselite.domain.repository.SettingsRepository
import com.amehran.expenselite.domain.usecase.ExportDataUseCase
import com.amehran.expenselite.domain.usecase.ImportDataUseCase
import com.amehran.expenselite.presentation.util.SnackbarController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val exportDataUseCase: ExportDataUseCase,
    private val importDataUseCase: ImportDataUseCase,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsState())
    val uiState: StateFlow<SettingsState> = _uiState.asStateFlow()

    val isDarkMode: StateFlow<Boolean> = settingsRepository.isDarkMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun exportData(outputStream: OutputStream) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            exportDataUseCase(outputStream).fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    SnackbarController.showMessage("Export successful")
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    SnackbarController.showMessage("Export failed: ${error.message}")
                },
            )
        }
    }

    fun importData(inputStream: InputStream) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            importDataUseCase(inputStream).fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    SnackbarController.showMessage("Import successful")
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    SnackbarController.showMessage("Import failed: ${error.message}")
                },
            )
        }
    }

    fun toggleDarkMode(isDark: Boolean) {
        viewModelScope.launch {
            settingsRepository.setDarkMode(isDark)
        }
    }
}

data class SettingsState(
    val isLoading: Boolean = false,
)
