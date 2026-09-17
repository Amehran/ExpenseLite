package com.amehran.expenselite.presentation.settings

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.amehran.expenselite.domain.usecase.ConflictStrategy
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SettingsScreen(
    onOpenDrawer: () -> Unit,
    onNavigateToCategoryManagement: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val context = LocalContext.current

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json"),
    ) { uri: Uri? ->
        uri?.let {
            context.contentResolver.openOutputStream(it)?.let { outputStream ->
                viewModel.exportData(outputStream)
            }
        }
    }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
    ) { uri: Uri? ->
        uri?.let { viewModel.onSelectImportFile(it) }
    }

    Scaffold(
        topBar = { SettingsTopAppBar(onOpenDrawer = onOpenDrawer) },
    ) { padding ->
        SettingsContent(
            modifier = Modifier.padding(padding),
            isDarkMode = isDarkMode,
            isLoading = uiState.isLoading,
            onToggleDarkMode = viewModel::toggleDarkMode,
            onNavigateToCategoryManagement = onNavigateToCategoryManagement,
            onExportClick = {
                val date = SimpleDateFormat("yyyyMMdd_HHmm", Locale.getDefault()).format(Date())
                exportLauncher.launch("expenselite_backup_$date.json")
            },
            onImportClick = { importLauncher.launch(arrayOf("application/json")) },
        )

        if (uiState.showConflictDialog && uiState.pendingImportUri != null) {
            val pendingUri = uiState.pendingImportUri!!
            ImportConflictDialog(
                onDismiss = viewModel::onDismissConflictDialog,
                onSelectStrategy = { strategy ->
                    context.contentResolver.openInputStream(pendingUri)?.let { inputStream ->
                        viewModel.onConfirmImport(strategy, inputStream)
                    }
                },
            )
        }
    }
}

@Composable
private fun SettingsContent(
    modifier: Modifier = Modifier,
    isDarkMode: Boolean,
    isLoading: Boolean,
    onToggleDarkMode: (Boolean) -> Unit,
    onNavigateToCategoryManagement: () -> Unit,
    onExportClick: () -> Unit,
    onImportClick: () -> Unit,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 600.dp)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
        PreferencesSection(
            isDarkMode = isDarkMode,
            onToggleDarkMode = onToggleDarkMode,
        )

        DataManagementSection(
            isLoading = isLoading,
            onNavigateToCategoryManagement = onNavigateToCategoryManagement,
            onExportClick = onExportClick,
            onImportClick = onImportClick,
        )

        if (isLoading) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsTopAppBar(onOpenDrawer: () -> Unit) {
    CenterAlignedTopAppBar(
        title = { Text("Settings", fontWeight = FontWeight.Bold) },
        navigationIcon = {
            IconButton(onClick = onOpenDrawer) {
                Icon(Icons.Default.Menu, contentDescription = "Menu")
            }
        },
    )
}

@Composable
private fun PreferencesSection(
    isDarkMode: Boolean,
    onToggleDarkMode: (Boolean) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Text(
                text = "Preferences",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(
                        text = "Dark Mode",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = "Enable dark background colors",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Switch(
                    checked = isDarkMode,
                    onCheckedChange = onToggleDarkMode,
                )
            }
        }
    }
}

@Composable
private fun DataManagementSection(
    isLoading: Boolean,
    onNavigateToCategoryManagement: () -> Unit,
    onExportClick: () -> Unit,
    onImportClick: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "Data Management",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )

            Button(
                onClick = onNavigateToCategoryManagement,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
            ) {
                Text("Manage Categories")
            }

            OutlinedButton(
                onClick = onExportClick,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading,
                shape = RoundedCornerShape(12.dp),
            ) {
                Text("Export Backup (JSON)")
            }

            Button(
                onClick = onImportClick,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
            ) {
                Text("Import Backup (JSON)")
            }
        }
    }
}

@Composable
private fun ImportConflictDialog(
    onDismiss: () -> Unit,
    onSelectStrategy: (ConflictStrategy) -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Import Backup", fontWeight = FontWeight.Bold) },
        text = {
            Text("How would you like to handle existing data during import?")
        },
        confirmButton = {
            Button(
                onClick = { onSelectStrategy(ConflictStrategy.MERGE) },
                shape = RoundedCornerShape(10.dp),
            ) {
                Text("Merge")
            }
        },
        dismissButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
                OutlinedButton(
                    onClick = { onSelectStrategy(ConflictStrategy.OVERWRITE) },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                    shape = RoundedCornerShape(10.dp),
                ) {
                    Text("Overwrite")
                }
            }
        },
    )
}
