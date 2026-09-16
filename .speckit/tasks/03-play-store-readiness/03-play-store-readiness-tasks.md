# Tasks: ExpenseLite Phase 03 (Play Store Readiness)

_Generated for Google Play Store publication readiness and compliance._
_Format: `[ ]` todo · `[/]` in-progress · `[x]` done_

---

- [x] **Task 1: R8 Proguard Rules & Serialization Keep Rules**
  - **Objective:** Ensure R8 minification does not strip Kotlinx Serialization models, Room entities, or Hilt bindings.
  - **Files:** `app/proguard-rules.pro`
  - **Verification:** `./gradlew assembleRelease` compiles cleanly without obfuscation errors.

- [x] **Task 2: Release Build Configuration Audit**
  - **Objective:** Verify `app/build.gradle.kts` release buildType settings, versionCode, and versionName.
  - **Files:** `app/build.gradle.kts`
  - **Verification:** `isMinifyEnabled = true` and `isShrinkResources = true` enabled.

- [x] **Task 3: Launcher Icons & App Metadata Check**
  - **Objective:** Verify app string resources and launcher icons in AndroidManifest.
  - **Files:** `app/src/main/AndroidManifest.xml`, `app/src/main/res/values/strings.xml`
  - **Verification:** App label and launcher icons configured cleanly.

- [x] **Task 4: Play Store Compliance Documentation (`PRIVACY.md`)**
  - **Objective:** Prepare compliance declarations required by Google Play Console policy.
  - **Files:** `PRIVACY.md`, `PLAY_CONSOLE_DECLARATIONS.md`
  - **Verification:** Verify that `PRIVACY.md` and `PLAY_CONSOLE_DECLARATIONS.md` describe offline-first zero data collection policy.

- [x] **Task 5: End-to-End Release Build Verification**
  - **Objective:** Validate release bundle build `.aab`.
  - **Files:** `app/build/outputs/bundle/release/app-release.aab`
  - **Verification:** Execute `./gradlew bundleRelease` and full test suite cleanly.
