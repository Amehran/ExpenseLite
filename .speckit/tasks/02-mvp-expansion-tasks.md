# Tasks: ExpenseLite Milestone 2 (MVP Expansion)
_Generated for the Insights, Navigation & Data Portability features._
_Format: `[ ]` todo · `[/]` in-progress · `[x]` done_

---

## Phase 1 — Database & Taxonomy (Room v1 → v2)
**Purpose**: Prepare database for category colors, names, and migration.

- [x] T201 Update `ExpenseEntity`: Add `categoryName` (String, default `""`) and `categoryColorHex` (String, default `""`).
- [ ] T202 Update `CategoryEntity`: Add `colorHex` (String) to support colors.
- [ ] T203 Create `MIGRATION_1_2` in `ExpenseDatabase` with `ALTER TABLE` scripts for `expenses` (add `categoryName`, `categoryColorHex`) and `categories` (add `colorHex`).
- [ ] T204 Bump database version to `2` and ensure `exportSchema = true` is set with the path configured in KSP.
- [ ] T205 Write Android instrumented test `MigrationTestHelper` to verify `MIGRATION_1_2` preserves existing data and populates new columns.
- [ ] T206 Update `DatabaseCallback` to seed the default categories with specific hex colors (Housing: #5C6BC0, Food: #4CAF50, etc.).
- [ ] T207 Update `ExpenseEntity.toDomain` and `Expense.toEntity` mappers to accommodate new fields. Update `AddTransactionUseCase` / ViewModel to copy category details onto the `ExpenseEntity` when saving.

---

## Phase 2 — App Shell & Navigation
**Purpose**: Hoist navigation state and introduce type-safe routes + Drawer.

- [ ] T208 Replace `Screen.kt` string routes with a `@Serializable sealed interface AppRoute` containing data objects for `DashboardRoute`, `AnalyticsRoute`, `SettingsRoute`, `CategoryRoute`, and `AddEditRoute(expenseId: String?)`.
- [ ] T209 Update `libs.versions.toml` and `build.gradle.kts` to ensure Navigation Compose `2.8.x` is used and Kotlinx Serialization plugin is applied.
- [ ] T210 Refactor `ExpenseNavHost` to use the new typed routes instead of string literals.
- [ ] T211 Create `AppShell` composable wrapping `ExpenseNavHost` with a `ModalNavigationDrawer` containing standard drawer items (Dashboard, Analytics, Settings).
- [ ] T212 Update `MainActivity` to render `AppShell` instead of directly rendering `ExpenseNavHost`.
- [ ] T213 Add Top App Bar to main screens with a hamburger menu icon wired to `DrawerState.open()`.

---

## Phase 3 — Dashboard Enhancements
**Purpose**: Monthly filters, chips, and cash flow insights.

- [ ] T214 Update `GetDashboardDataUseCase` and `ExpenseDao` to support filtering by month (start/end timestamp).
- [ ] T215 Implement "Previous/Next Month" chevrons in Dashboard Top App Bar to change the selected month offset.
- [ ] T216 Update `DashboardViewModel` to support an active filter state (`All`, `Income`, `Expense`, or `Category` ID) and derive "Monthly Burn Rate" and "Avg Daily Spend" for summary cards.
- [ ] T217 Add horizontally scrollable row of filter chips (`All`, `Income`, `Expense`, + dynamic categories) to `DashboardScreen` just below the summary cards.
- [ ] T218 Add inline search text field toggleable from the Top App Bar to live-filter the currently displayed list.

---

## Phase 4 — Analytics View Enhancements
**Purpose**: Month selection, empty states, and visual polishing.

- [ ] T219 Update `AnalyticsScreen` to include the month selector (Prev/Next chevrons) to change the `monthOffset`.
- [ ] T220 Update `DonutChart` rendering to use the new `categoryColorHex` from the DB instead of generating random/hardcoded colors.
- [ ] T221 Add a robust empty state for `AnalyticsScreen` when the selected month has zero expenses.

---

## Phase 5 — Data Portability (Export/Import with SAF)
**Purpose**: Resilient local backup with schema versions and conflict resolution.

- [ ] T222 Update `ExportDataUseCase` to serialize output as JSON with a `"schemaVersion": 2` top-level field.
- [ ] T223 Refactor `ImportDataUseCase` to parse the `"schemaVersion"` and accept a `ConflictStrategy` (`Merge` or `Overwrite`).
- [ ] T224 Ensure `ImportDataUseCase` executes all inserts wrapped inside a Room `@Transaction` block to ensure atomicity.
- [ ] T225 Update `SettingsViewModel` with state machine: `isExporting`, `isImporting`, `showConflictDialog`, `pendingImportUri`.
- [ ] T226 Update `SettingsScreen` to render an `AlertDialog` for Merge vs Overwrite selection when an import file is selected.
- [ ] T227 Add `ActivityResultLauncher` logic to handle SAF intents without keeping persistable permissions.

---

## Phase 6 — Verification & Polish
- [ ] T228 Run full test suite including unit tests and Compose rules.
- [ ] T229 Verify export→wipe→import round-trip works correctly using both Merge and Overwrite strategies.
- [ ] T230 Run `./gradlew ktlintCheck detekt assembleDebug bundleRelease` to verify CI standards and minification.
