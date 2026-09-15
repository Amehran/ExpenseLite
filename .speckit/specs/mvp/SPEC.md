---
spec_version: "1.0"
slug: expense-lite-mvp
date: "2026-09-14"
project: ExpenseLite
sources:
  - PRE_SPEC_INPUT provided in chat (2026-09-14)
companions:
  - stack.md
  - db-schema.md
  - screen-contracts.md
  - testing-strategy.md
assumptions:
  - Single system currency used as-is; no currency symbol configuration exposed in UI.
  - Uncategorized is a virtual reassignment target; architecture decides whether it is persisted as a CategoryEntity or handled in-memory.
open_questions:
  - "_bmad-output/tech-spec.md requested but is the canonical output of bmad-architecture, not bmad-spec — confirm whether bmad-architecture should run next."
  - "_bmad-output/epics-and-stories.md is produced by bmad-create-epics-and-stories — confirm whether to invoke after this spec."
---

# ExpenseLite MVP — SPEC

## Why

Demonstrate production-grade Android engineering skill through a fully local, offline-first personal finance tracker — no backend, no login — covering expense/income CRUD, recurring subscription management, analytics, and local backup, built to Modern Android Development (MAD) standards and verifiable by an automated CI/CD quality gate.

---

## Capabilities

### CAP-1 — Transaction CRUD
**Intent:** Users can create, edit, and delete financial transactions (expense or income) with a title, amount, category, date, and optional subscription flags.
**Success:** Create/edit/delete round-trips persist correctly in Room; all validation rules (amountCents > 0, date ≤ 1 year ahead) are enforced before save.

### CAP-2 — Recurring Subscription Tracking
**Intent:** Subscriptions with monthly or annual recurrence can be individually paused or resumed; pausing immediately removes the subscription's contribution from active monthly projections without deleting the entry or its history.
**Success:** Monthly projection = Σ(active monthly subscriptions) + Σ(active annual subscriptions / 12); paused subscriptions contribute 0; annual label preserved in subscription list view.

### CAP-3 — Home Dashboard
**Intent:** A summary screen shows the current month's total spend vs. projected subscription cost, plus a filterable list of recent transactions (by category chip and/or date range).
**Success:** All filter combinations return the correct subset; skeleton loaders appear during Loading state; empty state renders centered illustration + "Add Transaction" CTA when result set is empty.

### CAP-4 — Analytics View
**Intent:** A category distribution chart (rendered via custom Jetpack Compose Canvas) shows spending breakdown for a user-selected month, navigable by month offset.
**Success:** Chart proportions match CategorySpend data from ViewModel; OnMonthChanged correctly shifts the reporting window; chart re-renders on data change without recomposition artifacts.

### CAP-5 — Local Backup / Restore
**Intent:** Users can export all transactions and categories to JSON or CSV, and re-import from a previously exported file, using the system file picker (Storage Access Framework) with no runtime permissions.
**Success:** An export→import round-trip on a fresh install restores all ExpenseEntity and CategoryEntity records with identical field values; no permissions beyond SAF intent are declared in the manifest.

### CAP-6 — Category Management
**Intent:** Six pre-seeded system categories (Food, Housing, Transport, Entertainment, Utilities, Subscriptions) are always present and immutable; users can add and delete custom categories.
**Success:** System categories cannot be renamed or deleted; custom names validated for uniqueness (case-insensitive, no collision with system names); deleting a category with associated transactions presents a reassignment prompt before the delete commits.

### CAP-7 — App Settings
**Intent:** Users can toggle dark/light theme; the preference survives app restart.
**Success:** isDarkMode is persisted in Preferences DataStore and applied at process start via AppTheme before the first frame renders.

### CAP-8 — Automated Quality Pipeline
**Intent:** A GitHub Actions CI workflow runs on every push and pull request to main, executing formatting checks, static analysis, unit tests, a debug build, and a release bundle.
**Success:** Any ktlintCheck error, detekt violation, or unit test failure causes the workflow to exit non-zero and blocks merge; assembleDebug and bundleRelease (with R8) complete without error on a clean run.

---

## Constraints

- **Platform:** Android Min SDK 26 / Target SDK 35; Kotlin 2.x only; no XML views, no LiveData in UI layer, no un-scoped coroutines.
- **UI stack:** Jetpack Compose + Material 3; Single Activity; enableEdgeToEdge() called at Activity start.
- **Architecture:** Clean Architecture + MVVM; strict UDF; immutable StateFlow streams; state collected in UI via collectAsStateWithLifecycle().
- **DI:** Hilt throughout; @HiltViewModel + hiltViewModel() at nav entries; interfaces required for every Repository and DataSource to enable test-double injection.
- **Persistence:** Room (KSP annotation processor) for relational data; Preferences DataStore for key-value settings only.
- **Navigation:** Navigation Compose; routes defined as @Serializable data objects/classes for compile-time type safety.
- **File I/O:** Storage Access Framework exclusively; zero <uses-permission> for file access.
- **Money:** All amounts stored as Long in minor currency units (cents); Double/Float forbidden for monetary values; annual subscription divided by 12 only at projection calculation time.
- **Layer isolation:** ExpenseEntity/CategoryEntity must never appear in Composables or ViewModels; domain models (Expense, Category) are the cross-layer contract.
- **Screen state contracts:** Every feature screen defines sealed interfaces FeatureUiState, FeatureUiEvent, FeatureSideEffect; see screen-contracts.md.
- **Dispatcher injection:** Dispatchers.IO and Dispatchers.Default never hardcoded; injected as @IoDispatcher-qualified CoroutineDispatcher via Hilt.
- **Logging:** Zero android.util.Log calls; Timber only; DebugTree planted in debug builds, stripped in release via R8.
- **Observability:** Firebase Crashlytics for non-fatal domain errors in release builds only; no other Firebase products.
- **Build gate:** ./gradlew ktlintCheck detekt must succeed; failures are never suppressed.
- **Validation:** amountCents > 0; transaction date ≤ today + 365 days; custom category names unique case-insensitively and cannot match any system category name.

---

## Non-Goals

- Bank account syncing or any third-party financial data API (e.g., Plaid).
- Multi-currency support; a single system currency is assumed throughout.
- User authentication, sign-in flows, or account management of any kind.
- Cloud sync, Firebase Firestore, Firebase Auth, or any remote data persistence.
- Push notifications or background scheduling.

---

## Success Signal

1. CI pipeline passes (lint, detekt, unit tests, debug build, release bundle) on every PR with no suppressed errors.
2. 100% unit test coverage for all domain Use Cases; ViewModel StateFlow emissions verified with Turbine.
3. All three screens render correct Loading, Success, and Error states under createComposeRule() tests.
4. CAP-5 export→import round-trip on a clean device produces bit-identical records.
5. CAP-2 pause correctly zeroes a subscription's contribution to monthly projection, verified by CalculateMonthlyTotalUseCase unit test.
6. App compiles with minifyEnabled = true (release bundle) without missing keep rules.
