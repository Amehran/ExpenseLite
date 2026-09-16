# Tasks: ExpenseLite MVP

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and basic structure

- [ ] T001 Create Android project structure (core, data, domain, presentation) in app/src/main/java/com/example/expenselite/
- [ ] T002 Initialize Kotlin project with Jetpack Compose, Room, Hilt, Navigation dependencies in app/build.gradle.kts
- [ ] T003 [P] Configure detekt and linting in app/build.gradle.kts

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [x] T004 Setup Room Database (`ExpenseDatabase.kt`) and basic migration framework in app/src/main/java/com/example/expenselite/data/local/ExpenseDatabase.kt
- [x] T005 [P] Setup Hilt Application class (`ExpenseLiteApp.kt`) in app/src/main/java/com/example/expenselite/ExpenseLiteApp.kt
- [x] T006 [P] Create `Category` entity with constraints (name unique, isSystemDefault) in app/src/main/java/com/example/expenselite/data/local/entity/CategoryEntity.kt
- [x] T007 [P] Create `Expense` entity with constraints (amountCents > 0) in app/src/main/java/com/example/expenselite/data/local/entity/ExpenseEntity.kt
- [x] T008 Configure Room DAOs (`CategoryDao.kt`, `ExpenseDao.kt`) in app/src/main/java/com/example/expenselite/data/local/dao/CategoryDao.kt
- [x] T009 Seed Room DB with 6 immutable system categories (including ID 1 "Uncategorized") in Room callback in app/src/main/java/com/example/expenselite/data/local/DatabaseCallback.kt

**Checkpoint**: Foundation ready - user story implementation can now begin in parallel

---

## Phase 3: User Story 1 - Transaction Management (Priority: P1) 🎯 MVP

**Goal**: Manually enter expenses and income to track spending.

**Independent Test**: Can fully create, read, update, delete an expense and verify persistence.

### Tests for User Story 1

- [ ] T010 [P] [US1] Unit test `AddTransactionUseCase` in app/src/test/java/com/example/expenselite/domain/usecase/AddTransactionUseCaseTest.kt

### Implementation for User Story 1

- [x] T011 [P] [US1] Create Domain Models (`Category`, `Expense`) in app/src/main/java/com/example/expenselite/domain/model/Models.kt
- [x] T012 [P] [US1] Create `TransactionRepository` interface in app/src/main/java/com/example/expenselite/domain/repository/TransactionRepository.kt
- [x] T013 [US1] Implement `TransactionRepositoryImpl` mapping entities to domain models in app/src/main/java/com/example/expenselite/data/repository/TransactionRepositoryImpl.kt
- [x] T014 [US1] Implement `AddTransactionUseCase` in app/src/main/java/com/example/expenselite/domain/usecase/AddTransactionUseCase.kt
- [x] T015 [US1] Create `AddTransactionViewModel` in app/src/main/java/com/example/expenselite/presentation/transaction/AddTransactionViewModel.kt
- [x] T016 [US1] Implement Compose UI `AddTransactionScreen` in app/src/main/java/com/example/expenselite/presentation/transaction/AddTransactionScreen.kt

**Checkpoint**: At this point, User Story 1 should be fully functional and testable independently

---

## Phase 4: User Story 3 - Dashboard & Filtering (Priority: P1)

**Goal**: View a summary of monthly spend and filter transactions.

**Independent Test**: Dashboard displays total spend and allows filtering by category.

### Implementation for User Story 3

- [x] T017 [US3] Create `GetDashboardDataUseCase` in app/src/main/java/com/example/expenselite/domain/usecase/GetDashboardDataUseCase.kt
- [x] T018 [US3] Create `DashboardViewModel` in app/src/main/java/com/example/expenselite/presentation/dashboard/DashboardViewModel.kt
- [x] T019 [US3] Implement Compose UI `DashboardScreen` in app/src/main/java/com/example/expenselite/presentation/dashboard/DashboardScreen.kt

---

## Phase 5: User Story 2 - Recurring Subscriptions (Priority: P2)

**Goal**: Track recurring subscriptions and pause them.

**Independent Test**: Add a monthly/yearly subscription and verify the monthly projected total.

### Implementation for User Story 2

- [x] T020 [P] [US2] Update `AddTransactionScreen` to include recurrence UI (NONE, MONTHLY, YEARLY) in app/src/main/java/com/example/expenselite/presentation/transaction/AddTransactionScreen.kt
- [x] T021 [US2] Create `CalculateProjectedSubscriptionsUseCase` in app/src/main/java/com/example/expenselite/domain/usecase/CalculateProjectedSubscriptionsUseCase.kt
- [x] T022 [US2] Update `DashboardScreen` to display projected subscriptions total in app/src/main/java/com/example/expenselite/presentation/dashboard/DashboardScreen.kt

---

## Phase 6: User Story 5 - Local Backup/Restore (Priority: P2)

**Goal**: Export and import data locally without invasive permissions.

**Independent Test**: Export data via SAF, clear app data, import file to restore records.

### Implementation for User Story 5

- [x] T023 [P] [US5] Implement `ExportDataUseCase` (JSON serialization of all Room data) in app/src/main/java/com/example/expenselite/domain/usecase/ExportDataUseCase.kt
- [x] T024 [P] [US5] Implement `ImportDataUseCase` (JSON deserialization and DB overwrite) in app/src/main/java/com/example/expenselite/domain/usecase/ImportDataUseCase.kt
- [x] T025 [US5] Create `SettingsViewModel` to handle SAF streams in app/src/main/java/com/example/expenselite/presentation/settings/SettingsViewModel.kt
- [x] T026 [US5] Implement `SettingsScreen` with SAF `CreateDocument`/`OpenDocument` launchers in app/src/main/java/com/example/expenselite/presentation/settings/SettingsScreen.kt

---

## Phase 7: User Story 6 - Category Management (Priority: P3)

**Goal**: Create custom categories and prevent deletion of system ones.

**Independent Test**: Create custom category, attempt to delete system category (prevented).

### Implementation for User Story 6

- [x] T027 [P] [US6] Create `DeleteCategoryUseCase` (reassigns expenses to ID 1) in app/src/main/java/com/example/expenselite/domain/usecase/DeleteCategoryUseCase.kt
- [x] T028 [US6] Create `CategoryManagementViewModel` in app/src/main/java/com/example/expenselite/presentation/category/CategoryManagementViewModel.kt
- [x] T029 [US6] Implement `CategoryManagementScreen` in app/src/main/java/com/example/expenselite/presentation/category/CategoryManagementScreen.kt

---

## Phase 8: User Story 4 - Analytics View (Priority: P3)

**Goal**: See a category distribution chart for any given month.

**Independent Test**: Canvas chart renders proportions matching data.

### Implementation for User Story 4

- [x] T030 [P] [US4] Create `GetAnalyticsDataUseCase` in app/src/main/java/com/example/expenselite/domain/usecase/GetAnalyticsDataUseCase.kt
- [x] T031 [US4] Implement `DonutChart` Custom Compose Canvas in app/src/main/java/com/example/expenselite/presentation/analytics/components/DonutChart.kt
- [x] T032 [US4] Implement `AnalyticsScreen` combining the chart and legends in app/src/main/java/com/example/expenselite/presentation/analytics/AnalyticsScreen.kt

---

## Phase 9: Polish & Cross-Cutting Concerns

**Purpose**: Improvements that affect multiple user stories

- [x] T033 [P] Implement Dark/Light theme toggle via Preferences DataStore in app/src/main/java/com/example/expenselite/data/local/PreferencesManager.kt
- [x] T034 [P] Setup App Navigation Graph (NavHost) wrapping all screens above in app/src/main/java/com/example/expenselite/presentation/navigation/ExpenseNavHost.kt
- [x] T035 Add global error handling and Snackbar notifications in app/src/main/java/com/example/expenselite/presentation/MainActivity.kt
- [x] T036 Run `quickstart.md` validation scenarios manually

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion - BLOCKS all user stories
- **User Stories (Phase 3+)**: All depend on Foundational phase completion
  - User stories can then proceed in parallel (if staffed)
  - Or sequentially in priority order (P1 → P2 → P3)
- **Polish (Final Phase)**: Depends on all desired user stories being complete

### User Story Dependencies

- **User Story 1 (P1)**: Can start after Foundational (Phase 2)
- **User Story 3 (P1)**: Can start after Foundational (Phase 2)
- **User Story 2 (P2)**: Can start after US1/US3
- **User Story 5 (P2)**: Can start after US1
- **User Story 6 (P3)**: Can start after US1
- **User Story 4 (P3)**: Can start after US1/US3

### Parallel Opportunities

- Setup tasks marked [P] can run in parallel
- Foundational tasks marked [P] can run in parallel
- Once Foundational phase completes, all user stories can start in parallel (if team capacity allows)

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup
2. Complete Phase 2: Foundational (CRITICAL)
3. Complete Phase 3: User Story 1
4. **STOP and VALIDATE**: Test User Story 1 independently
