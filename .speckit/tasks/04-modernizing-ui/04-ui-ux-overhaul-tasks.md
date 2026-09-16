# Tasks: ExpenseLite Phase 04 (UI/UX Redesign & Premium Visual Overhaul)

_Generated for modernizing ExpenseLite UI with premium Material 3 aesthetics, curated fintech color palettes, elevated hero cards, and polished category badges._
_Format: `[ ]` todo · `[/]` in-progress · `[x]` done_

---

## Phase 1 — Color System & Theme Modernization
**Purpose**: Replace raw/default colors with a modern, high-contrast fintech palette.

- [x] **T401 Update `Color.kt`**: Define premium color tokens:
  - Slate Background: `#F8F9FA` (Light), `#121316` (Dark)
  - Primary Accent: Indigo `#4F46E5` / `#6366F1`
  - Income Accent: Emerald Green `#10B981`
  - Expense Accent: Rose/Red `#EF4444`
  - Card Surface: `#FFFFFF` (Light), `#1E2026` (Dark)
- [x] **T402 Update `Theme.kt`**: Refactor `LightColorScheme` and `DarkColorScheme` to apply surface tones, container colors, and outline variants.

---

## Phase 2 — Hero Cards & Dashboard Polish
**Purpose**: Transforming the plain white Dashboard into an engaging visual layout.

- [x] **T403 Elevate Summary Cards in `DashboardScreen.kt`**:
  - Replace flat boxes with gradient-backed, elevated cards (20dp corner radius).
  - Add distinct typography scaling for Total Balance vs. Monthly Burn Rate.
- [x] **T404 Refactor Transaction List Items**:
  - Wrap transaction rows in elevated Surface cards with 12dp rounded corners.
  - Add circular category icon badges with matching background color tints.

---

## Phase 3 — Analytics & Chart Screen Polish
**Purpose**: Give the Analytics view a sleek, modern visual presentation.

- [x] **T405 Enhance DonutChart & Legend Layout in `AnalyticsScreen.kt`**:
  - Place DonutChart inside a clean Surface card with drop shadow / elevation.
  - Redesign legend items into styled pills with matching color dots and percentage badges.
- [x] **T406 Refactor Analytics Empty State**:
  - Create a polished empty state card with modern vector illustration/icon and soft background tint.

---

## Phase 4 — Category & Settings Screen Redesign
**Purpose**: Unify app-wide visual quality across management and settings screens.

- [x] **T407 Redesign `CategoryManagementScreen.kt`**:
  - Format category items as styled cards with color pickers and delete action buttons.
- [x] **T408 Polish `SettingsScreen.kt`**:
  - Group settings into elevated Card sections (Appearance, Data Management, About).
  - Style Export/Import buttons with distinct primary and outlined button variants.

---

## Phase 5 — Verification & Build Quality
**Purpose**: Ensure code quality and test coverage remain uncompromised.

- [x] **T409 Run verification suite**:
  - Execute `./gradlew spotlessCheck detekt test jacocoTestReport assembleDebug bundleRelease`.
