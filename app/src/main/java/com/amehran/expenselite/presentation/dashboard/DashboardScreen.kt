package com.amehran.expenselite.presentation.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.amehran.expenselite.domain.model.Expense
import com.amehran.expenselite.ui.theme.Emerald500
import com.amehran.expenselite.ui.theme.Indigo600
import com.amehran.expenselite.ui.theme.Rose500
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(
    onNavigateToAnalytics: () -> Unit,
    onNavigateToAddTransaction: () -> Unit = {},
    onEditTransaction: (Long) -> Unit = {},
    onOpenDrawer: () -> Unit = {},
    drawerState: DrawerState? = null,
    viewModel: DashboardViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            DashboardTopAppBar(
                uiState = uiState,
                onOpenDrawer = {
                    if (drawerState != null) {
                        scope.launch { drawerState.open() }
                    } else {
                        onOpenDrawer()
                    }
                },
                onSelectPreviousMonth = { viewModel.selectPreviousMonth() },
                onSelectNextMonth = { viewModel.selectNextMonth() },
                onToggleSearch = { viewModel.toggleSearch() },
                onSearchQueryChange = { viewModel.setSearchQuery(it) },
                onNavigateToAnalytics = onNavigateToAnalytics,
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddTransaction,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                shape = CircleShape,
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Transaction")
            }
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            when (val state = uiState) {
                is DashboardState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                is DashboardState.Success -> {
                    DashboardContent(
                        state = state,
                        onFilterSelected = { viewModel.setFilter(it) },
                        onEditTransaction = onEditTransaction,
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
    CenterAlignedTopAppBar(
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
                Icon(Icons.Default.Menu, contentDescription = "Open Navigation Drawer")
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
    val state = uiState as? DashboardState.Success ?: return
    if (state.isSearchActive) {
        OutlinedTextField(
            value = state.searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .padding(end = 8.dp),
            placeholder = { Text("Search transactions...") },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
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
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous Month")
        }
        Text(
            text = selectedMonthLabel,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
        IconButton(onClick = onSelectNextMonth) {
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next Month")
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
    onEditTransaction: (Long) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        SummaryHeroCard(state)

        FilterChipRow(
            availableFilters = state.availableFilters,
            activeFilter = state.activeFilter,
            onFilterSelected = onFilterSelected,
        )

        Text(
            text = "Recent Transactions",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
        )

        if (state.recentTransactions.isEmpty()) {
            EmptyTransactionsPlaceholder()
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(state.recentTransactions, key = { it.id }) { expense ->
                    TransactionItem(
                        expense = expense,
                        onEditTransaction = onEditTransaction,
                    )
                }
            }
        }
    }
}

@Composable
private fun SummaryHeroCard(state: DashboardState.Success) {
    val gradientBrush = Brush.linearGradient(
        colors = listOf(
            Indigo600,
            Color(0xFF3730A3),
        ),
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(gradientBrush)
                .padding(20.dp),
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Total Balance",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White.copy(alpha = 0.8f),
                )
                Text(
                    text = state.totalBalance,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Income", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.8f))
                        Text(state.totalIncome, color = Emerald500, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Expense", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.8f))
                        Text(state.totalExpense, color = Rose500, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterChipRow(
    availableFilters: List<TransactionFilter>,
    activeFilter: TransactionFilter,
    onFilterSelected: (TransactionFilter) -> Unit,
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(availableFilters) { filter ->
            val label = when (filter) {
                is TransactionFilter.All -> "All"
                is TransactionFilter.Income -> "Income"
                is TransactionFilter.Expense -> "Expense"
                is TransactionFilter.Category -> filter.name.ifBlank { "Uncategorized" }
            }
            FilterChip(
                selected = activeFilter == filter,
                onClick = { onFilterSelected(filter) },
                label = { Text(label) },
                shape = RoundedCornerShape(12.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ),
            )
        }
    }
}

@Composable
private fun EmptyTransactionsPlaceholder() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "No transactions found for this period.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun TransactionItem(
    expense: Expense,
    onEditTransaction: (Long) -> Unit,
) {
    val formatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    val dateString = formatter.format(Date(expense.timestamp))
    val amountColor = if (expense.isIncome) Emerald500 else Rose500
    val sign = if (expense.isIncome) "+" else "-"
    val amountString = String.format("%s$%.2f", sign, expense.amountCents / 100.0)

    val categoryColor = parseColor(expense.categoryColorHex)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEditTransaction(expense.id) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                CategoryAvatarBadge(
                    categoryName = expense.categoryName.ifBlank { expense.title },
                    color = categoryColor,
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = expense.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = "$dateString • ${expense.categoryName.ifBlank { "Uncategorized" }}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
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

@Composable
private fun CategoryAvatarBadge(categoryName: String, color: Color) {
    val initial = categoryName.firstOrNull()?.uppercaseChar()?.toString() ?: "T"
    Box(
        modifier = Modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(color.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = initial,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color,
        )
    }
}

private fun parseColor(hex: String): Color {
    return runCatching {
        if (hex.isNotBlank() && hex.startsWith("#")) {
            Color(android.graphics.Color.parseColor(hex))
        } else {
            Indigo600
        }
    }.getOrDefault(Indigo600)
}
