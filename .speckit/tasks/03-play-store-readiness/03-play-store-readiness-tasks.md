# 03-play-store-readiness-tasks.md — Play Store Submission & Release Hardening

## Overview
This task list defines the steps required to transition ExpenseLite from Milestone 2 to a hardened, compliant release build ready for the Google Play Console, adhering to the architecture constraints in `stack.md`[cite: 4] and capabilities in `SPEC.md`[cite: 5].

---

## Tasks

- [ ] **Task 1: Production Signing & R8 Minification Hardening**
  - **Objective:** Configure release build shrinkage, obfuscation, resource stripping, and key signing in Gradle[cite: 4, 5].
  - **Files:** `app/build.gradle.kts`, `app/proguard-rules.pro`
  - **Steps:**
    1. Define `signingConfigs.create("release")` in `app/build.gradle.kts` consuming `KEYSTORE_PATH`, `KEYSTORE_PASSWORD`, `KEY_ALIAS`, and `KEY_PASSWORD` from environment variables or a local `keystore.properties` file.
    2. Set `isMinifyEnabled = true` and `isShrinkResources = true` on the `release` build type block in `app/build.gradle.kts`[cite: 4, 5].
    3. Add ProGuard keep rules in `app/proguard-rules.pro` for KotlinX Serialization navigation routes (`@Serializable`)[cite: 2, 4], Room entity and database schemas[cite: 3], and Hilt generated components[cite: 4].
  - **Verification:** Execute `./gradlew clean bundleRelease`[cite: 4, 5]. Build must generate a valid `.aab` file without R8 missing rule warnings or compilation failures[cite: 5].

---

- [ ] **Task 2: Splash Screen & Launcher Asset Integration**
  - **Objective:** Provide visual assets and handle cold-start transitions on Android 12+ (API 31+).
  - **Files:** `app/build.gradle.kts`, `app/src/main/res/values/themes.xml`, `app/src/main/res/mipmap-*/`, `app/src/main/java/.../MainActivity.kt`
  - **Steps:**
    1. Add `androidx.core:core-splashscreen` to `libs.versions.toml` and declare dependency in `app/build.gradle.kts`.
    2. Implement `Theme.App.Starting` in `themes.xml` extending `Theme.SplashScreen` with the branded background color and vector icon.
    3. Call `installSplashScreen()` in `MainActivity.onCreate()` immediately before `enableEdgeToEdge()`[cite: 4].
    4. Provide adaptive launcher icons (`ic_launcher.xml` and `ic_launcher_round.xml`) with foreground and background vector assets in `res/mipmap-anydpi-v26/`.
  - **Verification:** Cold-boot the app on an API 31+ emulator/device. The splash screen must transition cleanly into the Compose UI with no visual artifacts or blank frames.

---

- [ ] **Task 3: Permission Audit & Manifest Validation**
  - **Objective:** Verify zero unnecessary permissions exist in the manifest to ensure frictionless Play Store review[cite: 4, 5].
  - **Files:** `app/src/main/AndroidManifest.xml`
  - **Steps:**
    1. Audit `AndroidManifest.xml` to ensure zero `<uses-permission>` tags are declared for storage or networking[cite: 4, 5].
    2. Confirm all file export and import mechanisms use the Storage Access Framework (SAF) exclusively (`ACTION_CREATE_DOCUMENT`, `ACTION_OPEN_DOCUMENT`)[cite: 4, 5].
    3. Inspect the merged manifest output at `app/build/intermediates/merged_manifest/release/processReleaseMainManifest/AndroidManifest.xml` to confirm no third-party libraries injected background permissions.
  - **Verification:** Run `./gradlew processReleaseMainManifest` and check the resulting XML to confirm no unapproved Android permissions are present[cite: 4, 5].

---

- [ ] **Task 4: Play Store Compliance Documentation (`PRIVACY.md`)**
  - **Objective:** Prepare compliance declarations required by Google Play Console policy.
  - **Files:** `PRIVACY.md`, `PLAY_CONSOLE_DECLARATIONS.md`
  - **Steps:**
    1. Create root `PRIVACY.md` stating that ExpenseLite is an offline-first application, stores financial data strictly on the local device using SQLite/Room[cite: 5], and collects zero telemetry, behavioral data, or user identifiers[cite: 5].
    2. Create `PLAY_CONSOLE_DECLARATIONS.md` outlining the exact answers for the Play Console questionnaire:
       - Data Safety: "Does your app collect or share any user data?" $\rightarrow$ **No**.
       - Financial Features: "Financial Management / Expense Tracking" (Offline-only, no banking API integration)[cite: 5].
       - Target Audience: "All ages / 18+".
  - **Verification:** Verify that `PRIVACY.md` renders cleanly as a publicly accessible markdown page.

---

- [ ] **Task 5: End-to-End Release Build Verification**
  - **Objective:** Validate that the minified release build behaves identically to the debug build without reflection or serialization failures[cite: 5].
  - **Files:** `app/build/outputs/bundle/release/app-release.aab`
  - **Steps:**
    1. Build the release bundle using `./gradlew bundleRelease`[cite: 4, 5].
    2. Generate an APK set using `bundletool` and install it onto a connected device:
       ```bash
       bundletool build-apks --bundle=app/build/outputs/bundle/release/app-release.aab --output=app-release.apks --mode=universal
       bundletool install-apks --apks=app-release.apks
       ```
    3. Execute the full offline-first workflow on the release binary:
       - Add, edit, and delete an expense transaction[cite: 5].
       - Navigate between Dashboard, Analytics, and Settings via the Navigation Drawer[cite: 2].
       - Export database contents to a JSON document via SAF[cite: 4, 5].
       - Clear app data and restore transactions using the exported JSON file[cite: 5].
  - **Verification:** All screen transitions, database operations, and SAF file operations complete without throwing `ClassNotFoundException`, `MissingFieldException`, or `SQLiteException`[cite: 5].