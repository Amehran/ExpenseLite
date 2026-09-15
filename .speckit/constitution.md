# ExpenseLite - Project Constitution

## Purpose
ExpenseLite is an open-source, privacy-focused personal finance tracker built with **100% Kotlin 2.x** and modern Android technologies. The project emphasizes clean architecture, testability, and a premium user experience.

## Technology Stack

### Core Framework
- **Language**: 100% Kotlin 2.x
- **UI Toolkit**: Jetpack Compose (Modern UI, no XML layouts)
- **Design System**: Material 3
- **Architecture**:
  - Clean Architecture Principles
  - MVVM (Model-View-ViewModel) with **StateFlow** (UDF - Unidirectional Data Flow)
  - Single-Activity Architecture
- **Dependency Injection**: Hilt (via KSP)

### Data Layer
- **Database**: Jetpack Room with **Kotlin Symbol Processing (KSP)**
- **Repository Pattern**: Abstract repositories with suspend functions
- **Data Transfer Objects (DTOs)**: Kotlin data classes

### Concurrency
- **No hardcoded Dispatchers** (Use `DispatcherProvider` abstraction)
- Use `coroutineScope`, `async`, and `await` for concurrent operations
- Flow operators: `map`, `flatMapLatest`, `filter`, `combine`
- Safe collection: `collectAsStateWithLifecycle` in UI

### Jetpack Libraries
- Navigation Compose
- ViewModel (via Hilt)
- Lifecycle Compose
- DataStore (or Room for preferences)

## Design Principles

### Architecture & Organization
- **Layered Architecture**:
  - `presentation`: Composables, ViewModels
  - `domain`: Use Cases, Repository Interfaces, Entities
  - `data`: Repository Implementations, Room Database/DAOs, Remote Data Sources (if applicable)
  - `di`: Hilt Modules
- **Modularization**:
  - `app` (Core)
  - Feature modules (e.g., `feature-expenses`, `feature-insights`)
- **Single Responsibility**:
  - One ViewModel per screen/major feature
  - One Repository per data source
  - Clean, focused Use Cases

## Core Architectural Guardrails
- Language: 100% Kotlin 2.x (Explicit visibility, immutability by default).
- UI: Jetpack Compose + Material 3, Single Activity, `enableEdgeToEdge()`.
- Architecture: Clean Architecture + MVVM + Unidirectional Data Flow (UDF).
- State & Asynchrony: Coroutines + `StateFlow` (`collectAsStateWithLifecycle()` in UI).
- Dependency Injection: Hilt with `@HiltViewModel`.
- Data Layer: Room DB with KSP + Preferences DataStore.
- Safety: Room entities/DTOs must NEVER cross into UI Composables.
- Async Safety: Injected `CoroutineDispatcher` (no hardcoded `Dispatchers.IO`).

### State Management
- **Unidirectional Data Flow (UDF)**:
  ```kotlin
  sealed class UiState { ... }
  sealed class UserIntent { ... }
  
  // ViewModel
  val uiState: StateFlow<UiState>
  fun handleIntent(intent: UserIntent)
  ```
- **Immutable State**: Use Kotlin's immutability features
- **Derived State**: Combine Flows using `combine` instead of manual derivation in Composables

### Performance & Best Practices
- **Lazy Loading**: Use `LazyColumn`, `LazyRow` for long lists
- **State Hoisting**: Lift state to parent Composables when needed
- **Recomposition Optimization**: Use `key` in loops, avoid unnecessary `remember` recalculations
- **Resource Management**: Close resources when done (e.g., `Flow.cancellable`)

### Code Quality
- **Readability**: Descriptive variable names, meaningful comments
- **Testability**:
  - Dependency Injection for easy mocking
  - Repository interfaces instead of concrete classes
  - Pure functions where possible
- **Type Safety**: Prefer Kotlin's type system over workarounds

## Development Guidelines

### Branching Strategy (GitFlow-lite)
- `main` - Production-ready code
- `develop` - Integration branch
- Feature branches: `feature/expense-tracking`, `feature/budgeting`, etc.

### Commit Messages
- **Format**: Conventional Commits (optional but recommended)
  ```
  feat: add expense tracking with Room
  ```
- **Style**: Imperative mood (e.g., "Fix bug" not "Fixed bug")
- **Clarity**: Describe what and why

### Code Review Checklist
- [ ] 100% Kotlin 2.x syntax
- [ ] No hardcoded Dispatchers (use `DispatcherProvider`)
- [ ] UDF state management pattern used
- [ ] `@Composable` functions are pure and stateless where possible
- [ ] Proper Flow collection with `collectAsStateWithLifecycle`
- [ ] Hilt injection used correctly
- [ ] Room with KSP (no annotation processor)
- [ ] Jetpack Compose best practices followed
- [ ] Project follows Clean Architecture layers
- [ ] Meaningful variable and function names

## Documentation
- Add KDoc comments for public APIs
- Document complex business logic in Use Cases
- Keep `README.md` updated
- Document architecture decisions in `/docs` directory

## Common Patterns

### ViewModel with StateFlow (UDF)
```kotlin
@HiltViewModel
class MyViewModel(
    private val useCase: MyUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
    
    private val _intent = Channel<UserIntent>(Channel.UNLIMITED)
    
    init {
        viewModelScope.launch {
            _intent.receiveAsFlow().collectLatest { intent ->
                when (intent) {
                    is UserIntent.LoadData -> loadData()
                    is UserIntent.SelectItem -> selectItem(intent.id)
                }
            }
        }
    }
    
    fun handleIntent(intent: UserIntent) {
        viewModelScope.launch {
            _intent.send(intent)
        }
    }
    
    private fun loadData() = viewModelScope.launch {
        _uiState.value = UiState.Loading
        _uiState.value = try {
            val data = useCase.getData()
            UiState.Success(data)
        } catch (e: Exception) {
            UiState.Error(e)
        }
    }
}
```

### Data Layer with Room & KSP
```kotlin
@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val amount: Double,
    val date: Long
)

@Dao
interface ExpenseDao {
    @Query("SELECT * FROM expenses ORDER BY date DESC")
    fun getAllExpenses(): Flow<List<ExpenseEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(expense: ExpenseEntity): Long
}
```

### Dependency Injection (Hilt + KSP)
```kotlin
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "expense_database"
        ).addMigrations(MIGRATION_1_2)
        .build()
    }
    
    @Provides
    fun provideExpenseDao(database: AppDatabase): ExpenseDao {
        return database.expenseDao()
    }
}
```

## Maintenance
- Periodically review outdated dependencies
- Refactor code when Kotlin/Jetpack libraries release new versions
- Update database migrations when schema changes
- Remove commented code and unused imports

## License
ExpenseLite is open-source software released under the [MIT License](https://opensource.org/licenses/MIT) or [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0) (to be determined, but Apache 2.0 preferred for open-source projects).