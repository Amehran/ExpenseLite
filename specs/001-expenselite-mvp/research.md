# Phase 0: Outline & Research

## Unknowns & Clarifications

1. **How to implement the custom Canvas chart for Analytics?**
   - *Decision*: Use Jetpack Compose `Canvas` with `drawArc` for a pie/donut chart.
   - *Rationale*: Native Compose drawing is highly performant and avoids adding third-party charting libraries for a simple MVP feature.
   - *Alternatives considered*: MPAndroidChart, Vico. Rejected to keep the dependency footprint small and fully embrace Compose.

2. **How to manage export/import with Storage Access Framework (SAF)?**
   - *Decision*: Use `ActivityResultContracts.CreateDocument` (export) and `OpenDocument` (import) inside the Activity/Compose layer, passing Streams down to the data layer or parsing JSON in a ViewModel/UseCase.
   - *Rationale*: Avoids needing `READ/WRITE_EXTERNAL_STORAGE` runtime permissions, aligning with modern Android privacy standards.
   - *Alternatives considered*: File provider or direct file access. Rejected due to Scoped Storage restrictions.

3. **How to handle "Uncategorized" category deletion/reassignment?**
   - *Decision*: Seed the Room DB with an immutable system category (`id = 1`, `name = "Uncategorized"`, `isSystemDefault = true`). When deleting a custom category, a UseCase will update all associated expenses to `categoryId = 1`.
   - *Rationale*: Keeps referential integrity simple (no nullable category IDs needed on Expenses).
   - *Alternatives considered*: Nullable `categoryId` in the Expense table. Rejected because explicit default categories are easier to query and manage in UI.

4. **How to store monetary amounts?**
   - *Decision*: Store all amounts as `Long` representing minor units (e.g., cents).
   - *Rationale*: Standard practice to prevent floating-point precision issues.
   - *Alternatives considered*: `BigDecimal`. Rejected because `Long` is simpler for SQLite and sufficient for MVP scale.
