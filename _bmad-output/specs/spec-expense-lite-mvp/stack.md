# stack.md — Technology Stack & Architectural Guardrails
_Companion to SPEC.md (spec-expense-lite-mvp). Read alongside SPEC.md for the full contract._

## Technology Stack

| Layer / Concern | Library / Tool | Strict Guideline |
|---|---|---|
| **Language** | Kotlin 2.x | 100% idiomatic; explicit visibility modifiers on every declaration; `val` by default, `var` only where mutation is required |
| **UI Framework** | Jetpack Compose + Material 3 | Single Activity architecture; `enableEdgeToEdge()` called unconditionally at activity start |
| **Architecture Pattern** | Clean Architecture + MVVM | Strict layer boundaries: Data → Domain → UI; no upward leakage of entities |
| **Async & State** | Kotlin Coroutines + StateFlow | UI state collected via `collectAsStateWithLifecycle()`; no `GlobalScope`; no `runBlocking` in production code |
| **Dependency Injection** | Hilt | `@HiltViewModel` on every ViewModel; `hiltViewModel()` at NavGraph entry points; interfaces required for all Repositories and DataSources |
| **Database** | Room (KSP) | KSP annotation processor only (no KAPT); all DAOs return `Flow<T>` for reactive reads |
| **Settings Persistence** | Preferences DataStore | Key-value store for app settings only; no complex objects in DataStore |
| **Navigation** | Navigation Compose | Type-safe routes via `@Serializable` data objects/classes; `NavHost` declared in `MainActivity` |
| **File I/O** | Storage Access Framework (SAF) | `ACTION_CREATE_DOCUMENT` for export; `ACTION_OPEN_DOCUMENT` for import; zero `<uses-permission>` in manifest for file access |
| **Static Analysis** | Ktlint + Detekt | Ktlint enforces official Kotlin style; Detekt configured with cyclomatic complexity ≤ 10, no unused imports, no unhandled coroutine exceptions |
| **CI/CD** | GitHub Actions | Workflow file: `.github/workflows/android_ci.yml`; triggers: `push` and `pull_request` to `main` |
| **Crash Monitoring** | Firebase Crashlytics | Release builds only; non-fatal domain errors reported via `FirebaseCrashlytics.getInstance().recordException(e)`; initialized in `Application.onCreate()` |
| **Logging** | Timber | `Timber.plant(Timber.DebugTree())` in debug `Application`; `Timber.plant(CrashlyticsTree())` in release; `android.util.Log` usage is a build-breaking detekt rule |
| **Test Framework (Unit)** | JUnit 5 + MockK + Turbine | Use Cases: 100% coverage; ViewModels: `StandardTestDispatcher` + `Turbine` for StateFlow assertions |
| **Test Framework (UI)** | Compose Testing (`createComposeRule`) | Stateless composable tests only; no Espresso |
| **Test Framework (DB)** | Room in-memory (`inMemoryDatabaseBuilder`) | DAO queries and entity-to-domain mapper accuracy |
| **Build System** | Gradle (Kotlin DSL) | `build.gradle.kts` throughout; `libs.versions.toml` for version catalog |

## Architectural Layers

```
app/
├── data/
│   ├── local/
│   │   ├── dao/          ← Room DAOs (return Flow<T>)
│   │   ├── entity/       ← Room @Entity classes (never leave data layer)
│   │   └── mapper/       ← Entity ↔ Domain model mappers
│   ├── repository/       ← Repository implementations (implement domain interfaces)
│   └── di/               ← Hilt modules for data layer
├── domain/
│   ├── model/            ← Pure Kotlin domain models (Expense, Category, MonthlySummary)
│   ├── repository/       ← Repository interfaces
│   └── usecase/          ← Use Cases (single-responsibility, injectable)
└── ui/
    ├── navigation/       ← NavGraph, route definitions (@Serializable)
    ├── feature/
    │   ├── dashboard/    ← DashboardViewModel, DashboardScreen, DashboardUiState
    │   ├── analytics/    ← AnalyticsViewModel, AnalyticsScreen
    │   ├── addedit/      ← AddEditViewModel, AddEditScreen
    │   ├── settings/     ← SettingsViewModel, SettingsScreen
    │   └── categories/   ← CategoryViewModel, CategoryScreen
    └── theme/            ← MaterialTheme, AppTheme composable
```

## Dispatcher Injection Contract

All coroutine dispatcher dependencies must be injected via Hilt qualifiers. Hardcoding `Dispatchers.IO` or `Dispatchers.Default` anywhere in production code is a build-breaking detekt rule.

```kotlin
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IoDispatcher

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DefaultDispatcher

// Provided in a Hilt @Module:
@Provides @IoDispatcher
fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO
```

## CI/CD Pipeline — GitHub Actions

File: `.github/workflows/android_ci.yml`

**Trigger:** `push` and `pull_request` targeting `main`

**Steps (in order):**
1. `actions/checkout` + JDK 17 setup
2. Gradle cache restore
3. `./gradlew ktlintCheck detekt` — fails build on any violation
4. `./gradlew testDebugUnitTest` — fails build on any test failure
5. `./gradlew assembleDebug` — verifies debug compilation
6. `./gradlew bundleRelease` — verifies R8/ProGuard minification
