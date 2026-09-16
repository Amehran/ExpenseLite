# Tasks: ExpenseLite MVP
_Generated from `.speckit/specs/mvp/SPEC.md` and companions._
_Format: `[ ]` todo · `[/]` in-progress · `[x]` done_

---

## Phase 1 — Foundation (Blocking prerequisite for all features)

**Purpose:** Project skeleton, build tooling, DI, Room, Navigation — nothing feature-specific.

- [ ] T001 Create Android project with `app` module, Kotlin DSL `build.gradle.kts`, and `libs.versions.toml` version catalog
- [ ] T002 Add dependencies: Compose BOM, Material 3, Hilt (KSP), Room (KSP), Navigation Compose (2.8+), DataStore Preferences, Kotlinx Serialization, Timber, Turbine, MockK, JUnit 5
- [ ] T003 [P] Apply plugins: `com.google.devtools.ksp`, `dagger.hilt.android.plugin`, `kotlinx-serialization`
- [ ] T004 [P] Configure Ktlint + Detekt: no `android.util.Log`, no hardcoded dispatchers, cyclomatic complexity ≤ 10, trailing commas, no wildcard imports
- [ ] T005 [P] Configure `spotless` to wrap Ktlint and run on `preBuild`
- [ ] T006 [P] Create `ExpenseLiteApp : Application()` with `@HiltAndroidApp`; plant `Timber.DebugTree()` in debug, `CrashlyticsTree()` stub in release
- [ ] T007 Create `@IoDispatcher` and `@DefaultDispatcher` Hilt qualifiers in `core/di/DispatcherModule.kt`
- [ ] T008 [P] Define `CategoryEntity` (`id: Long PK autoGenerate`, `name: String UNIQUE`, `iconResName: String`, `isSystemDefault: Boolean`) in `data/local/entity/`
- [ ] T009 [P] Define `ExpenseEntity` (`id: Long PK autoGenerate`, `title`, `amountCents: Long`, `categoryId: Long FK`, `timestamp: Long`, `isIncome`, `isSubscription`, `recurrenceInterval: String`, `isPaused`) in `data/local/entity/`
- [ ] T010 Create `CategoryDao` with: `getAllCategories(): Flow<List<CategoryEntity>>`, `insert`, `deleteById` in `data/local/dao/`
- [ ] T011 Create `ExpenseDao` with: `getAllExpenses(): Flow<List<ExpenseEntity>>`, `getExpensesByDateRange(start, end)`, `getSubscriptions()`, `insert`, `update`, `deleteById` in `data/local/dao/`
- [ ] T012 Create `ExpenseDatabase` (`@Database version=1, exportSchema=true`); register `CategoryDao`, `ExpenseDao`; configure `room.schemaLocation` KSP arg in `build.gradle.kts`
- [ ] T013 Implement `DatabaseCallback` that seeds 6 system categories (Food, Housing, Transport, Entertainment, Utilities, Subscriptions) with fixed IDs — idempotent via `INSERT OR IGNORE`
- [ ] T014 Create `DatabaseModule` (Hilt `@Singleton`): provides `ExpenseDatabase`, `CategoryDao`, `ExpenseDao`, dispatchers
- [ ] T015 Define domain models: `Category`, `Expense`, `RecurrenceInterval (enum)`, `MonthlySummary`, `CategorySpend` in `domain/model/`
- [ ] T016 [P] Create entity<->domain mapper functions in `data/local/mapper/`: `ExpenseEntity.toDomain(category)`, `Expense.toEntity()`, `CategoryEntity.toDomain()`, `Category.toEntity()`
- [ ] T017 Define `@Serializable` route objects: `DashboardRoute`, `AnalyticsRoute`, `AddEditRoute(expenseId: String?)`, `SettingsRoute`, `CategoryRoute` in `presentation/navigation/`
- [ ] T018 Create `ExpenseNavHost` composable wiring all 5 routes to their screens; `startDestination = DashboardRoute`
- [ ] T019 Create `MainActivity` (`@AndroidEntryPoint`): calls `enableEdgeToEdge()`, hosts `ExpenseLiteTheme`, global `SnackbarHost`, `ExpenseNavHost`

**Checkpoint:** Hilt graph compiles; `assembleDebug` succeeds; Room schema JSON exported.

---

## Phase 2 — CAP-1: Transaction CRUD (P1)

**Purpose:** Create, read, update, delete expenses and income.

### Domain

- [ ] T020 Define `TransactionRepository` interface: `addExpense`, `updateExpense`, `deleteExpense`, `getExpense(id)`, `getAllExpenses(): Flow` in `domain/repository/`
- [ ] T021 Implement `TransactionRepositoryImpl` mapping entities; inject `@IoDispatcher` for all suspend calls
- [ ] T022 Implement `AddTransactionUseCase`: validates `amountCents > 0`, `date <= today + 365d`, delegates to repository
- [ ] T023 Implement `UpdateTransactionUseCase`
- [ ] T024 Implement `DeleteTransactionUseCase`
- [ ] T025 Implement `GetTransactionUseCase(id)`: returns `Expense?`

### Presentation

- [ ] T026 Define `AddEditUiState`, `AddEditUiEvent`, `AddEditSideEffect` sealed interfaces per `screen-contracts.md`
- [ ] T027 Implement `AddEditViewModel` (`@HiltViewModel`): loads categories, handles all `AddEditUiEvent` variants, emits `SideEffect` via `Channel`
- [ ] T028 Implement `AddEditScreen` composable: title field, amount field (cents parsing), category dropdown, date picker, income toggle, subscription section (recurrence, pause), Save/Delete buttons; collects `SideEffect` with `LaunchedEffect`

### Tests

- [ ] T029 [P] Unit test `AddTransactionUseCase`: `amountCents=0 -> failure`, `futureDate>365d -> failure`, `valid -> success`
- [ ] T030 [P] Unit test `AddEditViewModel` StateFlow with Turbine: all field change events update `Success` state; save with invalid input keeps error fields non-null
- [ ] T031 [P] Room DAO test: insert expense, query by id, update, delete — verify flow emissions

**Checkpoint:** Can create, edit, delete a transaction; validates correctly; ViewModel coverage passes.

---

## Phase 3 — CAP-3: Home Dashboard (P1)

**Purpose:** Summary screen with recent transactions, filter chip row, month browsing.

### Domain

- [ ] T032 Define `CategoryRepository` interface: `getAllCategories(): Flow<List<Category>>` in `domain/repository/`
- [ ] T033 Implement `CategoryRepositoryImpl`
- [ ] T034 Implement `GetDashboardDataUseCase`: takes `(startEpoch, endEpoch, categoryFilter?)`, returns `Flow<DashboardData>` combining expense totals + recent transactions
- [ ] T035 Implement `CalculateMonthlyTotalUseCase`: computes `MonthlySummary` (total spend + subscription projection); annual / 12 at calculation time; paused subscriptions contribute 0

### Presentation

- [ ] T036 Define `DashboardUiState`, `DashboardUiEvent`, `DashboardSideEffect` per `screen-contracts.md`
- [ ] T037 Implement `DashboardViewModel`: initializes to current month; handles `OnFilterCategory`, `OnDateRangeSelected`, `OnDeleteExpense`, `OnToggleSubscriptionPause`, `OnAddExpenseClicked`
- [ ] T038 Implement `DashboardScreen`: summary cards (total spend, subscription projection), skeleton loaders on `Loading`, empty state illustration + CTA on empty `Success`, filter chip row (All, Income, Expense, per-category chips), `LazyColumn` transaction list, FAB for add

### Tests

- [ ] T039 [P] Unit test `CalculateMonthlyTotalUseCase`: monthly only, annual/12, paused=0, mixed scenario
- [ ] T040 [P] Unit test `DashboardViewModel` Turbine: filter event filters list; toggle pause emits updated projection
- [ ] T041 [P] Compose UI test `DashboardScreen`: `Loading` -> skeletons; `Success(empty)` -> empty state; `Success(items)` -> list + card values

**Checkpoint:** Dashboard renders correctly for current month; category filter and subscription pause work.

---

## Phase 4 — CAP-2: Recurring Subscriptions (P1)

**Purpose:** Monthly/yearly subscriptions with pause/resume capability.

- [ ] T042 Implement `ToggleSubscriptionPauseUseCase(id)`: flips `isPaused`; updates via `TransactionRepository`
- [ ] T043 Implement `GetActiveSubscriptionsUseCase`: returns only `isSubscription=true && !isPaused`
- [ ] T044 Update `AddEditScreen` to show/hide subscription sub-section based on `isSubscription` toggle (recurrence interval selector, pause toggle visible when `isSubscription=true && isEditMode=true`)
- [ ] T045 Unit test `CalculateMonthlyTotalUseCase` with pause: active annual subscription contributes `amountCents/12`; paused -> 0

**Checkpoint:** Pausing a subscription zeroes its projection on Dashboard; `CalculateMonthlyTotalUseCase` unit test passes.

---

## Phase 5 — CAP-4: Analytics View (P3)

**Purpose:** Category donut chart + month navigation.

### Domain

- [ ] T046 Implement `GetAnalyticsDataUseCase(monthOffset: Int)`: queries expenses for the offset month; computes `List<CategorySpend>` with percentages (`totalCents / grandTotal`)

### Presentation

- [ ] T047 Define `AnalyticsUiState`, `AnalyticsUiEvent`, `AnalyticsSideEffect` per `screen-contracts.md`
- [ ] T048 Implement `AnalyticsViewModel`: default `monthOffset=0`; handles `OnMonthChanged`; re-triggers use case; emits Loading->Success/Error
- [ ] T049 Implement `DonutChart` composable: Compose Canvas arc segments proportional to `CategorySpend.percentage`, each arc colored by `categoryColorHex`; center label shows total spend
- [ ] T050 Implement `AnalyticsScreen`: month selector (Prev/Next chevrons + month label), `DonutChart`, `LazyColumn` legend (icon, name, total, percentage); `Empty` state when no expenses; month browsing disables "Next" at current month

### Tests

- [ ] T051 [P] Unit test `GetAnalyticsDataUseCase`: 3 categories -> percentages sum to 1f; zero expenses -> empty list; single category -> 100%
- [ ] T052 [P] Compose UI test `AnalyticsScreen`: `Loading` -> indicator; `Success` with categories -> segment labels present; `OnMonthChanged` event triggers when chevron tapped

**Checkpoint:** Donut chart renders; month navigation reloads data; percentages verified by unit test.

---

## Phase 6 — CAP-5: Local Backup / Restore (P2)

**Purpose:** Export/import JSON via SAF with conflict resolution.

### Domain

- [ ] T053 Define `BackupRepository` interface: `exportToStream(OutputStream)`, `importFromStream(InputStream, strategy: ConflictStrategy)`
- [ ] T054 Implement `ExportDataUseCase`: serializes all categories + expenses to JSON with `"schemaVersion": 1`; writes to injected `OutputStream` on `@IoDispatcher`
- [ ] T055 Implement `ImportDataUseCase(strategy: ConflictStrategy)`: validates `schemaVersion`, parses JSON; `Overwrite` -> delete non-system categories + all expenses then insert; `Merge` -> insert expenses/categories with IDs not present locally; wraps all DB ops in `@Transaction`
- [ ] T056 Define `ConflictStrategy` sealed class: `Overwrite`, `Merge`

### Presentation

- [ ] T057 Define full `SettingsUiState`, `SettingsUiEvent`, `SettingsSideEffect` per `screen-contracts.md` (add `isExporting`, `isImporting`, `showConflictDialog`, `pendingImportUri`)
- [ ] T058 Implement `SettingsViewModel`: handles export -> emits `LaunchFilePickerForExport` side effect; handles import file picked -> sets `pendingImportUri`, emits `showConflictDialog=true`; handles conflict strategy choice -> calls use case; emits Snackbar on result
- [ ] T059 Implement `SettingsScreen`: Export / Import buttons with `ActivityResultLauncher` (`CreateDocument` / `OpenDocument`); conflict resolution `AlertDialog` with Merge / Overwrite buttons; loading indicators while `isExporting` / `isImporting`

### Tests

- [ ] T060 [P] Unit test `ExportDataUseCase`: in-memory DB -> output stream -> JSON parses back; `schemaVersion` field present
- [ ] T061 [P] Unit test `ImportDataUseCase(Overwrite)`: existing records cleared; backup records inserted
- [ ] T062 [P] Unit test `ImportDataUseCase(Merge)`: existing ID skipped; new IDs inserted; system categories untouched
- [ ] T063 [P] Unit test `ImportDataUseCase` corrupt JSON -> `Result.failure`; wrong schemaVersion -> `SchemaMismatchException`

**Checkpoint:** Export->wipe->import round-trip produces identical records; conflict dialog appears; corrupt file rejected gracefully.

---

## Phase 7 — CAP-6: Category Management (P2)

**Purpose:** Add custom categories; protect system ones; reassign before delete.

### Domain

- [ ] T064 Implement `AddCategoryUseCase`: validates name non-empty, case-insensitively unique across all categories; inserts via `CategoryRepository`
- [ ] T065 Implement `DeleteCategoryUseCase`: blocks delete if `isSystemDefault=true`; counts affected expenses; if count > 0 returns count for caller to handle reassignment; reassigns expenses then deletes

### Presentation

- [ ] T066 Define `CategoryUiState`, `CategoryUiEvent`, `CategorySideEffect` per `screen-contracts.md`
- [ ] T067 Implement `CategoryManagementViewModel`: loads system/custom categories; handles `OnAddCategory`, `OnDeleteCategoryRequested` (emits `ShowReassignDialog` side effect if needed), `OnReassignAndDelete`
- [ ] T068 Implement `CategoryManagementScreen`: system categories list (non-deletable, visually distinct), custom categories list with delete swipe action, add category bottom sheet / dialog, reassignment dialog when delete has affected expenses

### Tests

- [ ] T069 [P] Unit test `AddCategoryUseCase`: duplicate name case-insensitive -> error; system name collision -> error; valid -> success
- [ ] T070 [P] Unit test `DeleteCategoryUseCase`: system category -> blocked; custom with no expenses -> deleted; custom with expenses -> returns count without deleting

**Checkpoint:** System categories cannot be deleted; reassignment dialog shown; uniqueness enforced case-insensitively.

---

## Phase 8 — CAP-7 Settings + CAP-8 Quality Gate

**Purpose:** Dark mode persistence, CI pipeline, Timber, Crashlytics stub, fake test infrastructure.

### Settings (CAP-7)

- [ ] T071 Implement `SettingsDataSource` interface + `PreferencesDataStoreImpl`: `isDarkMode: Flow<Boolean>`, `setDarkMode(Boolean)`, `lastBackupTimestamp: Flow<Long?>`, `setLastBackupTimestamp(Long)`
- [ ] T072 Update `MainActivity` to collect `isDarkMode` from `SettingsDataSource` and pass to `ExpenseLiteTheme`; persists across process restart
- [ ] T073 Wire dark mode toggle in `SettingsScreen` to `SettingsViewModel` -> `SettingsDataSource`

### CI/CD (CAP-8)

- [ ] T074 Create `.github/workflows/android_ci.yml`: triggers on `push` and `pull_request` to `main`; steps: checkout, JDK 17, Gradle cache, `ktlintCheck detekt`, `testDebugUnitTest`, `assembleDebug`, `bundleRelease`
- [ ] T075 Verify `bundleRelease` passes with `minifyEnabled = true`; add missing keep rules if R8 strips Hilt entry points or Room DAOs

### Fake Test Infrastructure

- [ ] T076 [P] Create `FakeExpenseRepository.kt` in `test/` source set: `MutableStateFlow<List<Expense>>`; implements all `TransactionRepository` methods
- [ ] T077 [P] Create `FakeCategoryRepository.kt` in `test/` source set
- [ ] T078 [P] Create `FakeSettingsDataSource.kt` in `test/` source set
- [ ] T079 [P] Create `FakeBackupRepository.kt` in `test/` source set

### Navigation Tests

- [ ] T080 [P] Navigation test: `OnAddExpenseClicked` -> `AddEditRoute(null)`; back from AddEdit -> `DashboardRoute`; Settings -> `CategoryRoute` works

### Final Validation

- [ ] T081 Run `./gradlew ktlintCheck detekt testDebugUnitTest assembleDebug bundleRelease` — all pass, zero suppressed violations
- [ ] T082 Manual E2E: export->wipe->import round-trip; dark mode toggle survives relaunch; subscription pause zeroes projection

---

## Dependencies & Execution Order

### Phase Dependencies

- **Phase 1 (Foundation)**: No dependencies — start immediately
- **Phase 2 (CRUD)**: Depends on Phase 1 completion
- **Phase 3 (Dashboard)**: Depends on Phase 1; can overlap with Phase 2 after T021
- **Phase 4 (Subscriptions)**: Depends on Phase 2 + Phase 3
- **Phase 5 (Analytics)**: Depends on Phase 2 + Phase 3
- **Phase 6 (Backup)**: Depends on Phase 2; can run after Phase 2
- **Phase 7 (Categories)**: Depends on Phase 2
- **Phase 8 (Polish)**: Depends on all phases complete

### Parallel Opportunities

- Tasks marked `[P]` within a phase can run in parallel after their phase's blocking tasks complete
- Phases 5, 6, 7 can proceed in parallel after Phase 2 + 3 complete

---

## Success Signals (from SPEC.md)

1. CI pipeline passes on every PR — no suppressed errors
2. 100% unit test coverage for all domain Use Cases; ViewModel `StateFlow` verified with Turbine
3. All 5 screens render `Loading`, `Success`, and `Error` states under `createComposeRule()` tests
4. CAP-5 export->import round-trip on a clean device produces bit-identical records
5. CAP-2 pause correctly zeroes subscription projection — verified by `CalculateMonthlyTotalUseCase` unit test
6. App compiles with `minifyEnabled = true` without missing keep rules
