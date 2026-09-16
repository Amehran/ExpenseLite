package com.amehran.expenselite.presentation.settings

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amehran.expenselite.domain.repository.SettingsRepository
import com.amehran.expenselite.domain.usecase.ConflictStrategy
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

private const val STOP_TIMEOUT_MILLIS = 5000L

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val exportDataUseCase: ExportDataUseCase,
    private val importDataUseCase: ImportDataUseCase,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsState())
    val uiState: StateFlow<SettingsState> = _uiState.asStateFlow()

    val isDarkMode: StateFlow<Boolean> = settingsRepository.isDarkMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), false)

    fun exportData(outputStream: OutputStream) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isExporting = true)
            exportDataUseCase(outputStream).fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(isExporting = false)
                    SnackbarController.showMessage("Export successful")
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(isExporting = false)
                    SnackbarController.showMessage("Export failed: ${error.message}")
                },
            )
        }
    }

    fun onSelectImportFile(uri: Uri) {
        _uiState.value = _uiState.value.copy(
            pendingImportUri = uri,
            showConflictDialog = true,
        )
    }

    fun onDismissConflictDialog() {
        _uiState.value = _uiState.value.copy(
            pendingImportUri = null,
            showConflictDialog = false,
        )
    }

    fun onConfirmImport(conflictStrategy: ConflictStrategy, inputStream: InputStream) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isImporting = true,
                showConflictDialog = false,
            )
            importDataUseCase(inputStream, conflictStrategy).fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        isImporting = false,
                        pendingImportUri = null,
                    )
                    SnackbarController.showMessage("Import successful")
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isImporting = false,
                        pendingImportUri = null,
                    )
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
    val isExporting: Boolean = false,
    val isImporting: Boolean = false,
    val showConflictDialog: Boolean = false,
    val pendingImportUri: Uri? = null,
) {
    val isLoading: Boolean
        get() = isExporting || isImporting
}
