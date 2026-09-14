# Phase 1: Quickstart Validation Guide

This guide describes how to validate the feature end-to-end once implemented.

## Prerequisites
- Android Studio Ladybug or later.
- Emulator or physical device running Android 8.0 (API 26) or higher.

## Setup
1. Clone the repository and open it in Android Studio.
2. Sync Gradle projects.
3. Build the project: `./gradlew assembleDebug`
4. Run detekt/lint: `./gradlew detekt lintDebug`
5. Run unit tests: `./gradlew testDebugUnitTest`

## End-to-End Validation Scenarios

### Scenario 1: Basic Tracking
1. Launch the app.
2. Observe the empty state on the Dashboard.
3. Tap "Add Transaction".
4. Enter Title="Groceries", Amount="50.00", Category="Food", Type="Expense".
5. Save the transaction.
6. Verify the Dashboard reflects "$50.00" in total monthly spend and the transaction appears in the recent list.

### Scenario 2: Subscription Projection
1. Tap "Add Transaction".
2. Enter Title="Netflix", Amount="15.00", Category="Entertainment", Type="Expense", Recurring="Monthly".
3. Save the transaction.
4. Verify the Dashboard shows "$15.00" in projected monthly subscriptions.
5. Edit the transaction and toggle it to "Paused".
6. Verify the projected monthly subscriptions drops to "$0.00".

### Scenario 3: Data Export/Import
1. Navigate to Settings -> "Export Data".
2. Select a location in the file picker.
3. Navigate to Settings -> "Import Data" and select the exported file.
4. Verify a success message is shown and the dashboard data remains intact and identical to before.

## Troubleshooting
- If tests fail, verify the Room schema export path is correctly configured in `build.gradle.kts`.
