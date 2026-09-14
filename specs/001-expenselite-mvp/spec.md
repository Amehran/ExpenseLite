# Feature Specification: ExpenseLite MVP

**Feature Branch**: `[001-expenselite-mvp]`

**Created**: 2026-09-14

**Status**: Draft

**Input**: User description: "PRE_SPEC_INPUT: ExpenseLite MVP..."

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Transaction Management (Priority: P1)
As a user, I want to manually enter my expenses and income so that I can track my spending.
**Why this priority**: Core functionality of an expense tracker.
**Independent Test**: Can be fully tested by creating, reading, updating, and deleting an expense and verifying it persists in the local database.
**Acceptance Scenarios**:
1. **Given** I am on the dashboard, **When** I add a valid expense, **Then** it appears in the recent transactions list.
2. **Given** an existing transaction, **When** I edit the amount, **Then** the updated amount is reflected in the dashboard.
3. **Given** an invalid amount (<=0), **When** I try to save, **Then** an error message is shown and the transaction is not saved.

### User Story 2 - Recurring Subscriptions (Priority: P2)
As a user, I want to track recurring subscriptions (monthly/yearly) and pause them if needed, so that I have an accurate projection of my fixed costs.
**Why this priority**: Differentiator for this MVP and required for accurate monthly projections.
**Independent Test**: Can be tested by adding a monthly and an annual subscription, then verifying the monthly projected total.
**Acceptance Scenarios**:
1. **Given** I add a monthly subscription of $10 and an annual of $120, **When** I view the dashboard, **Then** the projected subscription cost is $20.
2. **Given** an active subscription, **When** I toggle it to paused, **Then** its value is immediately subtracted from the active monthly budget projection.

### User Story 3 - Dashboard & Filtering (Priority: P1)
As a user, I want to view a summary of my monthly spend versus projected costs and filter my transactions, so that I understand my financial situation.
**Why this priority**: Primary view of the app.
**Independent Test**: Can be tested by populating mock data and verifying summary calculations and filter results.
**Acceptance Scenarios**:
1. **Given** multiple transactions across categories, **When** I select the "Food" chip, **Then** only Food transactions are displayed.
2. **Given** no transactions in a selected period, **When** the dashboard loads, **Then** the empty state illustration and "Add Transaction" CTA are shown.

### User Story 4 - Analytics View (Priority: P3)
As a user, I want to see a category distribution chart for any given month so that I know where my money goes.
**Why this priority**: Essential for deeper insights but secondary to basic tracking.
**Independent Test**: Can be tested by verifying the custom Canvas chart renders proportions matching the mock CategorySpend data.
**Acceptance Scenarios**:
1. **Given** spending in three categories, **When** I open Analytics, **Then** the chart displays three proportional segments.
2. **Given** I am viewing the current month, **When** I navigate to the previous month, **Then** the chart updates to reflect the previous month's data.

### User Story 5 - Local Backup/Restore (Priority: P2)
As a user, I want to export and import my data locally without granting invasive permissions, so that my data is safe and private.
**Why this priority**: Crucial for a local-first app without cloud sync.
**Independent Test**: Can be tested by exporting data to a file via SAF, clearing app data, and importing the file to restore all records.
**Acceptance Scenarios**:
1. **Given** existing transactions, **When** I trigger an export, **Then** a JSON/CSV file is saved using the system file picker.
2. **Given** an exported backup file, **When** I trigger import on a fresh install, **Then** all categories and transactions are restored perfectly.

### User Story 6 - Category Management (Priority: P3)
As a user, I want to use default categories or create my own, so that I can organize my spending my way.
**Why this priority**: Required for categorization, but system defaults cover most initial needs.
**Independent Test**: Can be tested by creating custom categories and attempting to delete system categories.
**Acceptance Scenarios**:
1. **Given** the settings screen, **When** I try to delete "Food" (system default), **Then** the action is prevented.
2. **Given** a custom category with associated transactions, **When** I delete it, **Then** I am prompted to reassign its transactions to "Uncategorized".

### Edge Cases

- What happens when a user tries to enter a transaction date more than 1 year in the future? (Blocked by validation)
- How does system handle currency precision? (Amounts are strictly stored as Long in minor units/cents)
- What happens when the user imports a backup that contains duplicate IDs or conflicting data? (Overwrites local data assuming backup is source of truth)

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST allow manual entry, editing, and deletion of expenses and income (amount > 0).
- **FR-002**: System MUST support recurring subscriptions (monthly/yearly) with active/paused states.
- **FR-003**: System MUST calculate monthly subscription projection as Σ(active monthly) + Σ(active annual / 12).
- **FR-004**: System MUST display a dashboard with monthly total spend, projected subscription costs, and a filterable recent transactions list.
- **FR-005**: System MUST provide an Analytics view with a custom Jetpack Compose Canvas chart showing category distribution for a selected month.
- **FR-006**: System MUST allow full local JSON and CSV export/import via Storage Access Framework (SAF) requiring no runtime permissions.
- **FR-007**: System MUST provide 6 immutable system categories and allow custom user-created categories (unique, case-insensitive).
- **FR-008**: System MUST prompt for reassignment to "Uncategorized" when deleting a category with associated transactions.
- **FR-009**: System MUST persist a Dark/Light theme toggle via Preferences DataStore.
- **FR-010**: System MUST NOT use external backend, user authentication, or cloud sync.
- **FR-011**: System MUST store all monetary amounts as `Long` in minor units (cents).

### Key Entities

- **Category**: Represents a spending bucket. Attributes: `id`, `name`, `iconResName`, `isSystemDefault`.
- **Expense**: Represents a transaction. Attributes: `id`, `title`, `amountCents`, `categoryId`, `timestamp`, `isIncome`, `isSubscription`, `recurrenceInterval`, `isPaused`.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: CI/CD pipeline (lint, detekt, unit tests, debug build, release bundle) passes on every PR with no suppressed errors.
- **SC-002**: 100% unit test coverage for all domain Use Cases.
- **SC-003**: Export/Import round-trip on a clean device produces bit-identical records with zero data loss.
- **SC-004**: App successfully compiles with `minifyEnabled = true` (R8/ProGuard) without missing keep rules.
- **SC-005**: All three primary screens successfully render Loading, Success, and Error states in automated Compose UI tests.

## Assumptions

- Single system currency is used as-is; no currency symbol configuration is exposed in the UI.
- "Uncategorized" is handled gracefully by the architecture as a reassignment target, either as a persisted entity or a virtual fallback.
- User data size remains within reasonable limits for local SQLite (Room) processing without extreme pagination requirements for MVP.
