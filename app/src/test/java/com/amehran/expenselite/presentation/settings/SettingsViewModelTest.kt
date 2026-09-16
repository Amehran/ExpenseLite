package com.amehran.expenselite.presentation.settings

import android.net.Uri
import com.amehran.expenselite.data.local.dao.CategoryDao
import com.amehran.expenselite.data.local.dao.ExpenseDao
import com.amehran.expenselite.data.local.entity.CategoryEntity
import com.amehran.expenselite.data.local.entity.ExpenseEntity
import com.amehran.expenselite.domain.repository.SettingsRepository
import com.amehran.expenselite.domain.usecase.ConflictStrategy
import com.amehran.expenselite.domain.usecase.ExportDataUseCase
import com.amehran.expenselite.domain.usecase.ImportDataUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeSettingsRepository: FakeSettingsRepository
    private lateinit var exportDataUseCase: ExportDataUseCase
    private lateinit var fakeImportDataUseCase: FakeImportDataUseCase
    private lateinit var viewModel: SettingsViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeSettingsRepository = FakeSettingsRepository()

        val fakeCategoryDao = object : CategoryDao {
            override fun getAllCategories(): Flow<List<CategoryEntity>> = flowOf(emptyList())
            override suspend fun insertCategory(category: CategoryEntity): Long = 1L
            override suspend fun updateCategory(category: CategoryEntity) {
                // Unused in tests
            }
            override suspend fun deleteCategory(category: CategoryEntity) {
                // Unused in tests
            }
            override suspend fun getCategoryById(id: Long): CategoryEntity? = null
            override suspend fun deleteNonSystemCategories() {
                // Unused in tests
            }
        }

        val fakeExpenseDao = object : ExpenseDao {
            override fun getAllExpenses(): Flow<List<ExpenseEntity>> = flowOf(emptyList())
            override fun getExpensesByDateRange(startTimestamp: Long, endTimestamp: Long): Flow<List<ExpenseEntity>> =
                flowOf(emptyList())
            override suspend fun insertExpense(expense: ExpenseEntity): Long = 1L
            override suspend fun updateExpense(expense: ExpenseEntity) {
                // Unused in tests
            }
            override suspend fun deleteExpense(expense: ExpenseEntity) {
                // Unused in tests
            }
            override suspend fun getExpenseById(id: Long): ExpenseEntity? = null
            override suspend fun reassignExpensesToUncategorized(oldCategoryId: Long) {
                // Unused in tests
            }
            override suspend fun deleteAllExpenses() {
                // Unused in tests
            }
        }

        exportDataUseCase = ExportDataUseCase(fakeCategoryDao, fakeExpenseDao)
        fakeImportDataUseCase = FakeImportDataUseCase()

        viewModel = SettingsViewModel(
            exportDataUseCase = exportDataUseCase,
            importDataUseCase = fakeImportDataUseCase,
            settingsRepository = fakeSettingsRepository,
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `exportData executes export and resets exporting state`() = runTest(testDispatcher) {
        val outputStream = ByteArrayOutputStream()
        viewModel.exportData(outputStream)

        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isExporting)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `onSelectImportFile updates pendingImportUri and shows conflict dialog`() {
        val mockUri = mock(Uri::class.java)
        viewModel.onSelectImportFile(mockUri)

        assertTrue(viewModel.uiState.value.showConflictDialog)
        assertEquals(mockUri, viewModel.uiState.value.pendingImportUri)
    }

    @Test
    fun `onDismissConflictDialog clears pendingImportUri and hides dialog`() {
        val mockUri = mock(Uri::class.java)
        viewModel.onSelectImportFile(mockUri)
        viewModel.onDismissConflictDialog()

        assertFalse(viewModel.uiState.value.showConflictDialog)
        assertNull(viewModel.uiState.value.pendingImportUri)
    }

    @Test
    fun `onConfirmImport executes import and resets state`() = runTest(testDispatcher) {
        val inputStream = ByteArrayInputStream("{}".toByteArray())
        viewModel.onConfirmImport(ConflictStrategy.OVERWRITE, inputStream)

        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(fakeImportDataUseCase.invoked)
        assertEquals(ConflictStrategy.OVERWRITE, fakeImportDataUseCase.lastStrategy)
        assertFalse(viewModel.uiState.value.isImporting)
        assertNull(viewModel.uiState.value.pendingImportUri)
    }

    @Test
    fun `toggleDarkMode updates settings repository`() = runTest(testDispatcher) {
        backgroundScope.launch { viewModel.isDarkMode.collect {} }
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.toggleDarkMode(true)
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.isDarkMode.value)
    }

    private class FakeSettingsRepository : SettingsRepository {
        private val _isDarkMode = MutableStateFlow(false)
        override val isDarkMode: Flow<Boolean> = _isDarkMode

        override suspend fun setDarkMode(isDark: Boolean) {
            _isDarkMode.value = isDark
        }
    }

    private class FakeImportDataUseCase : ImportDataUseCase(null, null, null) {
        var invoked = false
        var lastStrategy: ConflictStrategy? = null

        override suspend fun invoke(inputStream: InputStream, conflictStrategy: ConflictStrategy): Result<Unit> {
            invoked = true
            lastStrategy = conflictStrategy
            return Result.success(Unit)
        }
    }
}
