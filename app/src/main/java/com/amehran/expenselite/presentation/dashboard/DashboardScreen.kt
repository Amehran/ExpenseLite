package com.amehran.expenselite.presentation.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.amehran.expenselite.domain.model.Expense
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(
    onNavigateToAddTransaction: () -> Unit,
    onNavigateToAnalytics: () -> Unit,
    onOpenDrawer: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            DashboardTopAppBar(
                uiState = uiState,
                onOpenDrawer = onOpenDrawer,
                onSelectPreviousMonth = viewModel::selectPreviousMonth,
                onSelectNextMonth = viewModel::selectNextMonth,
                onToggleSearch = viewModel::toggleSearch,
                onSearchQueryChange = viewModel::setSearchQuery,
                onNavigateToAnalytics = onNavigateToAnalytics,
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToAddTransaction) {
                Icon(Icons.Default.Add, contentDescription = "Add Transaction")
            }
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            when (val state = uiState) {
                is DashboardState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is DashboardState.Success -> {
                    DashboardContent(
                        state = state,
                        onFilterSelected = viewModel::setFilter,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DashboardTopAppBar(
    uiState: DashboardState,
    onOpenDrawer: () -> Unit,
    onSelectPreviousMonth: () -> Unit,
    onSelectNextMonth: () -> Unit,
    onToggleSearch: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onNavigateToAnalytics: () -> Unit,
) {
    TopAppBar(
        title = {
            DashboardTopAppBarTitle(
                uiState = uiState,
                onSelectPreviousMonth = onSelectPreviousMonth,
                onSelectNextMonth = onSelectNextMonth,
                onSearchQueryChange = onSearchQueryChange,
            )
        },
        navigationIcon = {
            IconButton(onClick = onOpenDrawer) {
                Icon(Icons.Default.Menu, contentDescription = "Menu")
            }
        },
        actions = {
            DashboardTopAppBarActions(
                uiState = uiState,
                onToggleSearch = onToggleSearch,
                onNavigateToAnalytics = onNavigateToAnalytics,
            )
        },
    )
}

@Composable
private fun DashboardTopAppBarTitle(
    uiState: DashboardState,
    onSelectPreviousMonth: () -> Unit,
    onSelectNextMonth: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
) {
    val state = uiState as? DashboardState.Success ?: run {
        Text("ExpenseLite")
        return
    }

    if (state.isSearchActive) {
        OutlinedTextField(
            value = state.searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(end = 8.dp),
            placeholder = { Text("Search transactions...") },
            singleLine = true,
        )
    } else {
        MonthSelectorRow(
            selectedMonthLabel = state.selectedMonthLabel,
            onSelectPreviousMonth = onSelectPreviousMonth,
            onSelectNextMonth = onSelectNextMonth,
        )
    }
}

@Composable
private fun MonthSelectorRow(
    selectedMonthLabel: String,
    onSelectPreviousMonth: () -> Unit,
    onSelectNextMonth: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        IconButton(onClick = onSelectPreviousMonth) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Previous Month",
            )
        }
        Text(
            text = selectedMonthLabel,
            style = MaterialTheme.typography.titleMedium,
        )
        IconButton(onClick = onSelectNextMonth) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Next Month",
            )
        }
    }
}

@Composable
private fun DashboardTopAppBarActions(
    uiState: DashboardState,
    onToggleSearch: () -> Unit,
    onNavigateToAnalytics: () -> Unit,
) {
    val state = uiState as? DashboardState.Success ?: return

    if (state.isSearchActive) {
        IconButton(onClick = onToggleSearch) {
            Icon(Icons.Default.Close, contentDescription = "Close Search")
        }
    } else {
        IconButton(onClick = onToggleSearch) {
            Icon(Icons.Default.Search, contentDescription = "Search")
        }
        IconButton(onClick = onNavigateToAnalytics) {
            Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Analytics")
        }
    }
}

@Composable
private fun DashboardContent(
    state: DashboardState.Success,
    onFilterSelected: (TransactionFilter) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Balance Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text("Total Balance", style = MaterialTheme.typography.titleMedium)
                Text(
                    text = state.totalBalance,
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Income", style = MaterialTheme.typography.bodyMedium)
                        Text(state.totalIncome, color = Color(0xFF4CAF50), fontWeight = FontWeight.SemiBold)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Expense", style = MaterialTheme.typography.bodyMedium)
                        Text(
                            state.totalExpense,
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }
        }

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(state.availableFilters) { filter ->
                val label = when (filter) {
                    is TransactionFilter.All -> "All"
                    is TransactionFilter.Income -> "Income"
                    is TransactionFilter.Expense -> "Expense"
                    is TransactionFilter.Category -> filter.name.ifBlank { "Uncategorized" }
                }
                FilterChip(
                    selected = state.activeFilter == filter,
                    onClick = { onFilterSelected(filter) },
                    label = { Text(label) },
                )
            }
        }

        Text("Recent Transactions", style = MaterialTheme.typography.titleLarge)

        if (state.recentTransactions.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No transactions yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(state.recentTransactions, key = { it.id }) { expense ->
                    TransactionItem(expense = expense)
                }
            }
        }
    }
}

@Composable
private fun TransactionItem(expense: Expense) {
    val formatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    val dateString = formatter.format(Date(expense.timestamp))

    val amountColor = if (expense.isIncome) Color(0xFF4CAF50) else MaterialTheme.colorScheme.error
    val sign = if (expense.isIncome) "+" else "-"
    val amountString = String.format("%s$%.2f", sign, expense.amountCents / 100.0)

    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(expense.title, style = MaterialTheme.typography.titleMedium)
                Text(
                    dateString,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Text(
                text = amountString,
                style = MaterialTheme.typography.titleMedium,
                color = amountColor,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}
