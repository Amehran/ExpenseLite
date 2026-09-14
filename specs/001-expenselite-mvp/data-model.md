# Phase 1: Data Model

## Entities

### Category
Represents a bucket for categorizing expenses or income.
- `id` (Long, Primary Key, AutoGenerate)
- `name` (String, Unique, Case-insensitive constraint via UI validation)
- `iconResName` (String, reference to a local drawable/vector asset)
- `isSystemDefault` (Boolean) - True for the 6 immutable seed categories.

### Expense (Transaction)
Represents a single manual entry or a recurring subscription projection.
- `id` (Long, Primary Key, AutoGenerate)
- `title` (String)
- `amountCents` (Long) - Always stored as positive minor units.
- `categoryId` (Long, Foreign Key -> Category)
- `timestamp` (Long) - Epoch milliseconds.
- `isIncome` (Boolean) - If true, it's income. If false, it's an expense.
- `isSubscription` (Boolean) - If true, this represents a recurring subscription.
- `recurrenceInterval` (String, Enum: "NONE", "MONTHLY", "YEARLY")
- `isPaused` (Boolean) - Only applicable if `isSubscription` is true.

## Validation Rules
- `amountCents` MUST be > 0.
- Cannot delete a `Category` if `isSystemDefault` is true.
- When deleting a `Category`, its `Expense` records must be reassigned to `categoryId = 1` ("Uncategorized").

## State Transitions (Subscriptions)
- Subscriptions can toggle between `isPaused = false` (Active) and `isPaused = true` (Paused).
- When Active, a subscription contributes to the monthly projected total.
- When Paused, it is ignored in the projection calculations.
