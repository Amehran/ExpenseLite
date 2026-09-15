# testing-strategy.md — Testing Strategy & Testability Requirements
_Companion to SPEC.md (spec-expense-lite-mvp). Read alongside SPEC.md for the full contract._

## Coverage Targets

| Layer | Target | Tool |
|---|---|---|
| Domain Use Cases | 100% line + branch | JUnit 5 + MockK |
| ViewModels | All UiState emission paths | JUnit 5 + Turbine + StandardTestDispatcher |
| DAOs / Room | All queries + mapper accuracy | Room in-memory + JUnit 5 |
| Stateless Composables | Loading / Empty / Success / Error states | createComposeRule() |
| Navigation flows | Screen transitions + backstack | Fake NavController |

---

## Unit Testing — Domain Layer

Every Use Case must have a dedicated test class. MockK is the mocking library; JUnit 5 (`@Test`, `@BeforeEach`).

**Mandatory Use Cases to test (minimum):**

| Use Case | Key Assertions |
|---|---|
| `CalculateMonthlyTotalUseCase` | Monthly only, annual÷12, paused=0, mixed |
| `ProcessRecurringExpenseUseCase` | Monthly/annual flag propagation, pause effect |
| `ValidateTransactionUseCase` | amountCents=0 → error, future date > 1yr → error, valid → success |
| `AddCategoryUseCase` | Duplicate name (case-insensitive) → error, system name collision → error |
| `DeleteCategoryUseCase` | With affected transactions → returns count (caller decides next step) |

---

## Unit Testing — ViewModels

Use `StandardTestDispatcher` + `TestCoroutineScope`. Verify `StateFlow` emissions using Turbine's `awaitItem()`.

```kotlin
@Test fun `pausing subscription emits updated summary with zeroed projection`() = runTest {
    val viewModel = DashboardViewModel(fakeRepository, testDispatcher)
    viewModel.uiState.test {
        awaitItem() // Loading
        val success = awaitItem() as DashboardUiState.Success
        viewModel.onEvent(DashboardUiEvent.OnToggleSubscriptionPause("sub-123"))
        val updated = awaitItem() as DashboardUiState.Success
        assertThat(updated.summary.activeSubscriptionProjectionCents)
            .isLessThan(success.summary.activeSubscriptionProjectionCents)
    }
}
```

---

## Unit Testing — Data Layer (Room)

Use `Room.inMemoryDatabaseBuilder` with actual DAO implementations (no mocking).

Key test scenarios per DAO:

**ExpenseDao:**
- Insert and query by categoryId
- Filter by date range (timestamp between)
- getSubscriptions() returns only isSubscription=true rows
- Delete cascades correctly given RESTRICT FK (expect exception when category has expenses)
- Flow emits on insert/update/delete

**CategoryDao:**
- Unique name constraint throws on duplicate insert
- isSystemDefault rows survive delete attempt at DB level (enforced by business logic above)

**Mappers:**
- `ExpenseEntity.toDomain()` round-trips without data loss for all RecurrenceInterval values
- `null` / absent fields handled correctly

---

## UI Testing — Stateless Composables

Use `createComposeRule()`. Test stateless composable overloads that accept explicit state parameters.

**DashboardScreen tests:**
- `Loading` state → skeleton loaders visible, FAB present
- `Success` state with empty list → empty state illustration + CTA visible
- `Success` state with items → list items rendered, summary card values match input
- `Error` state → error message displayed

**AnalyticsScreen tests:**
- `Loading` → progress indicator visible
- `Success` → chart renders without crash; category labels present
- Month navigation buttons trigger `OnMonthChanged` event

**AddEditScreen tests:**
- Amount field with "0" → save blocked, error message visible
- Valid inputs → save button enabled
- Edit mode → fields pre-populated

---

## Fake Implementations (test source set)

Maintain a `test` source set with fake implementations of all repository interfaces. These are injected in ViewModel and Composable tests to avoid mocking overhead and database setup.

```
app/src/test/java/.../fake/
├── FakeExpenseRepository.kt        ← implements ExpenseRepository
├── FakeCategoryRepository.kt       ← implements CategoryRepository
├── FakeSettingsDataSource.kt       ← implements SettingsDataSource
└── FakeBackupRepository.kt         ← implements BackupRepository
```

Each fake maintains an in-memory `MutableStateFlow<List<T>>` and implements all interface methods. Test cases mutate the flow directly to simulate data changes.

---

## Navigation Tests

Use a `TestNavHostController` to verify:
- `DashboardUiEvent.OnAddExpenseClicked` → navigates to `AddEditRoute(expenseId = null)`
- `DashboardSideEffect.NavigateToAddEdit(expenseId = "abc")` → navigates to `AddEditRoute(expenseId = "abc")`
- Back from AddEdit → pops to Dashboard
- Settings → Category navigation works end-to-end

---

## Testability Design Rules (enforced)

1. **No `Dispatchers.IO` hardcoded** — inject `@IoDispatcher CoroutineDispatcher` via constructor; swap for `UnconfinedTestDispatcher` in tests.
2. **No singleton access** — no `object` repositories or `companion object` data sources; everything via Hilt injection.
3. **Fake over Mock where possible** — fakes are stateful and composable; mocks are for single-call verification only.
4. **Test naming:** `fun \`description of scenario\`()` — backtick names in English describing the scenario, not the implementation.
5. **Arrange-Act-Assert** structure in every test; no multiple assertions without explanation comment.
