# db-schema.md — Database Schema & Domain Models
_Companion to SPEC.md (spec-expense-lite-mvp). Read alongside SPEC.md for the full contract._

## Room Entities (Data Layer — never cross layer boundary)

### CategoryEntity

| Column | Type | Constraints |
|---|---|---|
| `id` | `String` | Primary Key (UUID) |
| `name` | `String` | Unique, non-null |
| `iconResName` | `String` | Android resource name identifier, non-null |
| `isSystemDefault` | `Boolean` | Default: `false` |

**Index:** UNIQUE on `name` (enforced at DB level).

### ExpenseEntity

| Column | Type | Constraints |
|---|---|---|
| `id` | `String` | Primary Key (UUID) |
| `title` | `String` | Non-null |
| `amountCents` | `Long` | Non-null; domain rule: must be > 0 |
| `categoryId` | `String` | Foreign Key → `CategoryEntity.id`; `onDelete = RESTRICT` |
| `timestamp` | `Long` | Epoch milliseconds; non-null |
| `isIncome` | `Boolean` | Default: `false` |
| `isSubscription` | `Boolean` | Default: `false` |
| `recurrenceInterval` | `String` | Enum string: `"MONTHLY"` \| `"YEARLY"` \| `"NONE"` |
| `isPaused` | `Boolean` | Default: `false`; only meaningful when `isSubscription = true` |

**Foreign key constraint:** `onDelete = RESTRICT` — deleting a category is blocked by Room if any expense references it; the UI must first reassign or the DAO call will throw `SQLiteConstraintException`.

**Index:** `categoryId` indexed for join performance.

## Domain Models (Cross-layer contract — used in Domain and UI layers)

### Category

```kotlin
data class Category(
    val id: String,
    val name: String,
    val iconResName: String,
    val isSystemDefault: Boolean,
)
```

### RecurrenceInterval

```kotlin
enum class RecurrenceInterval { MONTHLY, YEARLY, NONE }
```

### Expense

```kotlin
data class Expense(
    val id: String,
    val title: String,
    val amountCents: Long,        // always in minor units (cents)
    val category: Category,       // resolved from CategoryEntity join
    val timestamp: Long,          // epoch ms
    val isIncome: Boolean,
    val isSubscription: Boolean,
    val recurrenceInterval: RecurrenceInterval,
    val isPaused: Boolean,
)
```

### MonthlySummary (computed, not persisted)

```kotlin
data class MonthlySummary(
    val totalSpendCents: Long,            // sum of expense amountCents for current month
    val activeSubscriptionProjectionCents: Long, // Σ monthly + Σ(annual / 12), paused excluded
)
```

### CategorySpend (computed, not persisted)

```kotlin
data class CategorySpend(
    val category: Category,
    val totalCents: Long,
    val percentage: Float,   // 0f..1f; computed at use-case layer
)
```

## Entity → Domain Mappers (Data Layer boundary)

All mappers live in `data/local/mapper/`. Every DAO result is mapped before leaving the repository implementation. The mapper functions are the only place where `ExpenseEntity` and `CategoryEntity` are allowed to be referenced.

```kotlin
fun ExpenseEntity.toDomain(category: Category): Expense
fun Expense.toEntity(): ExpenseEntity
fun CategoryEntity.toDomain(): Category
fun Category.toEntity(): CategoryEntity
```

## Pre-Seeded System Categories

Inserted via a Room `Callback.onCreate` or a `RoomDatabase.prepopulateFromAsset` strategy. The six system categories use fixed, well-known UUIDs so re-seeding is idempotent.

| Name | isSystemDefault |
|---|---|
| Food | true |
| Housing | true |
| Transport | true |
| Entertainment | true |
| Utilities | true |
| Subscriptions | true |

## DataStore Keys (Settings)

Preferences DataStore key definitions live in a single `SettingsDataSource` interface implementation:

| Key | Type | Default |
|---|---|---|
| `dark_mode_enabled` | `Boolean` | `false` |
| `last_backup_timestamp` | `Long` | `null` (absent) |
