package com.amehran.expenselite.presentation.transaction

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.amehran.expenselite.domain.model.RecurrenceInterval

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    onNavigateBack: () -> Unit,
    viewModel: AddTransactionViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val categories by viewModel.categories.collectAsState()

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = if (uiState.isEditMode) "Edit Transaction" else "Add Transaction",
                        fontWeight = FontWeight.Bold,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (uiState.isEditMode) {
                        IconButton(onClick = { viewModel.onEvent(AddTransactionEvent.DeleteTransaction) }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Transaction",
                                tint = MaterialTheme.colorScheme.error,
                            )
                        }
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            OutlinedTextField(
                value = uiState.title,
                onValueChange = { viewModel.onEvent(AddTransactionEvent.OnTitleChanged(it)) },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
            )

            OutlinedTextField(
                value = uiState.amountString,
                onValueChange = { viewModel.onEvent(AddTransactionEvent.OnAmountChanged(it)) },
                label = { Text("Amount") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = !uiState.isIncome,
                    onClick = { viewModel.onEvent(AddTransactionEvent.OnTypeChanged(false)) },
                    label = { Text("Expense") },
                    shape = RoundedCornerShape(12.dp),
                )
                FilterChip(
                    selected = uiState.isIncome,
                    onClick = { viewModel.onEvent(AddTransactionEvent.OnTypeChanged(true)) },
                    label = { Text("Income") },
                    shape = RoundedCornerShape(12.dp),
                )
            }

            Text("Category", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categories) { category ->
                    FilterChip(
                        selected = uiState.selectedCategoryId == category.id,
                        onClick = { viewModel.onEvent(AddTransactionEvent.OnCategorySelected(category.id)) },
                        label = { Text(category.name) },
                        shape = RoundedCornerShape(12.dp),
                    )
                }
            }

            Text("Recurrence (Optional)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = uiState.recurrence == RecurrenceInterval.NONE,
                    onClick = { viewModel.onEvent(AddTransactionEvent.OnRecurrenceChanged(RecurrenceInterval.NONE)) },
                    label = { Text("None") },
                    shape = RoundedCornerShape(12.dp),
                )
                FilterChip(
                    selected = uiState.recurrence == RecurrenceInterval.MONTHLY,
                    onClick = { viewModel.onEvent(AddTransactionEvent.OnRecurrenceChanged(RecurrenceInterval.MONTHLY)) },
                    label = { Text("Monthly") },
                    shape = RoundedCornerShape(12.dp),
                )
                FilterChip(
                    selected = uiState.recurrence == RecurrenceInterval.YEARLY,
                    onClick = { viewModel.onEvent(AddTransactionEvent.OnRecurrenceChanged(RecurrenceInterval.YEARLY)) },
                    label = { Text("Yearly") },
                    shape = RoundedCornerShape(12.dp),
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { viewModel.onEvent(AddTransactionEvent.SaveTransaction) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading,
                shape = RoundedCornerShape(12.dp),
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                } else {
                    Text(if (uiState.isEditMode) "Update Transaction" else "Save Transaction")
                }
            }

            if (uiState.isEditMode) {
                OutlinedButton(
                    onClick = { viewModel.onEvent(AddTransactionEvent.DeleteTransaction) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isLoading,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                ) {
                    Text("Delete Transaction")
                }
            }
        }
    }
}
