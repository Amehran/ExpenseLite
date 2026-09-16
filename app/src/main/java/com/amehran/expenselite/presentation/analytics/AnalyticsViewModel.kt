package com.amehran.expenselite.presentation.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amehran.expenselite.domain.usecase.CategorySpend
import com.amehran.expenselite.domain.usecase.GetAnalyticsDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

sealed interface AnalyticsState {
    data object Loading : AnalyticsState

    data class Success(
        val selectedMonthLabel: String,
        val monthOffset: Int,
        val categorySpends: List<CategorySpend>,
    ) : AnalyticsState
}

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    private val getAnalyticsDataUseCase: GetAnalyticsDataUseCase,
) : ViewModel() {

    private val _monthOffset = MutableStateFlow(0)
    val monthOffset: StateFlow<Int> = _monthOffset.asStateFlow()

    val uiState: StateFlow<AnalyticsState> = _monthOffset
        .flatMapLatest { offset ->
            val yearMonth = YearMonth.now().plusMonths(offset.toLong())
            val year = yearMonth.year
            val monthIndex = yearMonth.monthValue - 1 // 0-indexed month for GetAnalyticsDataUseCase
            val monthLabel = getMonthLabel(offset)

            getAnalyticsDataUseCase(year, monthIndex).map { spends ->
                AnalyticsState.Success(
                    selectedMonthLabel = monthLabel,
                    monthOffset = offset,
                    categorySpends = spends,
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = AnalyticsState.Loading,
        )

    fun selectPreviousMonth() {
        _monthOffset.value -= 1
    }

    fun selectNextMonth() {
        _monthOffset.value += 1
    }

    fun previousMonth() = selectPreviousMonth()
    fun nextMonth() = selectNextMonth()

    private fun getMonthLabel(offset: Int): String {
        val yearMonth = YearMonth.now().plusMonths(offset.toLong())
        val formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault())
        return yearMonth.format(formatter)
    }
}

private const val STOP_TIMEOUT_MILLIS = 5000L
