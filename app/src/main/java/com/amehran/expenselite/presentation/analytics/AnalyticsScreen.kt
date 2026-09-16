package com.amehran.expenselite.presentation.analytics

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.amehran.expenselite.domain.usecase.CategorySpend
import com.amehran.expenselite.presentation.analytics.components.DonutChart
import java.util.Locale

@Suppress("MagicNumber")
private val fallbackPalette = listOf(
    Color(0xFF5C6BC0),
    Color(0xFF4CAF50),
    Color(0xFFFF9800),
    Color(0xFFE91E63),
    Color(0xFF9C27B0),
    Color(0xFF00BCD4),
    Color(0xFFFFC107),
    Color(0xFF795548),
)

fun parseCategoryColor(hex: String, fallbackIndex: Int): Color {
    if (hex.isNotBlank()) {
        try {
            val colorInt = android.graphics.Color.parseColor(if (hex.startsWith("#")) hex else "#$hex")
            return Color(colorInt)
        } catch (_: Exception) {
            // Ignore parse exception
        }
    }
    return fallbackPalette[fallbackIndex % fallbackPalette.size]
}

@Composable
fun AnalyticsScreen(
    onOpenDrawer: () -> Unit,
    viewModel: AnalyticsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            AnalyticsTopAppBar(onOpenDrawer = onOpenDrawer)
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            when (val state = uiState) {
                is AnalyticsState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is AnalyticsState.Success -> {
                    AnalyticsContent(
                        state = state,
                        onPreviousMonth = viewModel::selectPreviousMonth,
                        onNextMonth = viewModel::selectNextMonth,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AnalyticsTopAppBar(onOpenDrawer: () -> Unit) {
    CenterAlignedTopAppBar(
        title = { Text("Analytics", fontWeight = FontWeight.Bold) },
        navigationIcon = {
            IconButton(onClick = onOpenDrawer) {
                Icon(Icons.Default.Menu, contentDescription = "Menu")
            }
        },
    )
}

@Composable
private fun AnalyticsContent(
    state: AnalyticsState.Success,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
) {
    val dataWithColors = state.categorySpends.mapIndexed { index, spend ->
        Pair(spend, parseCategoryColor(spend.category.colorHex, index))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AnalyticsMonthHeader(
            selectedMonthLabel = state.selectedMonthLabel,
            onPreviousMonth = onPreviousMonth,
            onNextMonth = onNextMonth,
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (dataWithColors.isEmpty()) {
            AnalyticsEmptyState(selectedMonthLabel = state.selectedMonthLabel)
        } else {
            DonutChartCard(dataWithColors = dataWithColors)
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                items(dataWithColors, key = { it.first.category.id }) { (spend, color) ->
                    LegendItem(spend = spend, color = color)
                }
            }
        }
    }
}

@Composable
private fun DonutChartCard(dataWithColors: List<Pair<CategorySpend, Color>>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            contentAlignment = Alignment.Center,
        ) {
            DonutChart(data = dataWithColors)
        }
    }
}

@Composable
private fun AnalyticsMonthHeader(
    selectedMonthLabel: String,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onPreviousMonth) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous Month")
            }
            Text(
                text = selectedMonthLabel,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            IconButton(onClick = onNextMonth) {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next Month")
            }
        }
    }
}

@Composable
private fun AnalyticsEmptyState(selectedMonthLabel: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(32.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.List,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
            Text(
                text = "No Expenses Recorded",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "There are no expenses for $selectedMonthLabel.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Suppress("MagicNumber")
@Composable
private fun LegendItem(
    spend: CategorySpend,
    color: Color,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
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
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(color),
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = spend.category.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = String.format(Locale.US, "%.1f%%", spend.percentage),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            Text(
                text = String.format(Locale.US, "$%.2f", spend.amountCents / 100.0),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}
