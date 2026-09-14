package com.amehran.expenselite.presentation.transaction

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.amehran.expenselite.domain.model.RecurrenceInterval

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    onNavigateBack: () -> Unit,
    viewModel: AddTransactionViewModel = hiltViewModel()
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
            TopAppBar(title = { Text("Add Transaction") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (uiState.errorMessage != null) {
                Text(
                    text = uiState.errorMessage!!,
                    color = MaterialTheme.colorScheme.error
                )
            }

            OutlinedTextField(
                value = uiState.title,
                onValueChange = { viewModel.onEvent(AddTransactionEvent.OnTitleChanged(it)) },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.amountString,
                onValueChange = { viewModel.onEvent(AddTransactionEvent.OnAmountChanged(it)) },
                label = { Text("Amount") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = !uiState.isIncome,
                    onClick = { viewModel.onEvent(AddTransactionEvent.OnTypeChanged(false)) },
                    label = { Text("Expense") }
                )
                FilterChip(
                    selected = uiState.isIncome,
                    onClick = { viewModel.onEvent(AddTransactionEvent.OnTypeChanged(true)) },
                    label = { Text("Income") }
                )
            }

            Text("Category", style = MaterialTheme.typography.titleMedium)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categories) { category ->
                    FilterChip(
                        selected = uiState.selectedCategoryId == category.id,
                        onClick = { viewModel.onEvent(AddTransactionEvent.OnCategorySelected(category.id)) },
                        label = { Text(category.name) }
                    )
                }
            }

            // Subscriptions/Recurrence UI (T020 placeholder for now)
            Text("Recurrence (Optional)", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = uiState.recurrence == RecurrenceInterval.NONE,
                    onClick = { viewModel.onEvent(AddTransactionEvent.OnRecurrenceChanged(RecurrenceInterval.NONE)) },
                    label = { Text("None") }
                )
                FilterChip(
                    selected = uiState.recurrence == RecurrenceInterval.MONTHLY,
                    onClick = {
                        viewModel.onEvent(
                            AddTransactionEvent.OnRecurrenceChanged(RecurrenceInterval.MONTHLY)
                        )
                    },
                    label = { Text("Monthly") }
                )
                FilterChip(
                    selected = uiState.recurrence == RecurrenceInterval.YEARLY,
                    onClick = { viewModel.onEvent(AddTransactionEvent.OnRecurrenceChanged(RecurrenceInterval.YEARLY)) },
                    label = { Text("Yearly") }
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { viewModel.onEvent(AddTransactionEvent.SaveTransaction) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Save Transaction")
                }
            }
        }
    }
}
