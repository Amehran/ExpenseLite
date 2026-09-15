plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.detekt)
    alias(libs.plugins.spotless)
}

android {
    namespace = "com.amehran.expenselite"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.amehran.expenselite"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
}

// Spotless is a top-level extension (must be outside android { ... })
spotless {
    // Only check files modified since origin/dev
    ratchetFrom("origin/dev")

    kotlin {
        target("**/*.kt")
        targetExclude("$buildDir/**/*.kt")
        ktlint().editorConfigOverride(
            mapOf(
                "ktlint_function_naming_ignore_when_annotated_with" to "Composable",
                "ktlint_standard_value-parameter-comment" to "disabled",
                "ktlint_standard_value-argument-comment" to "disabled",
            ),
        )
    }
}
detekt {
    buildUponDefaultConfig = true
    ignoreFailures = true // Prevents Detekt from breaking the build on warnings
    config.setFrom(files("$rootDir/config/detekt/detekt.yml"))

    val baselineFile = file("$rootDir/config/detekt/baseline.xml")
    if (baselineFile.exists()) {
        baseline = baselineFile
    }
}
tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
    reports {
        html.required.set(true)
        html.outputLocation.set(layout.buildDirectory.file("reports/detekt/detekt.html"))

        xml.required.set(false)
        txt.required.set(false)
        sarif.required.set(false)
    }
}

dependencies {
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.core)
    // Core & Compose BOM
    detektPlugins(libs.detekt.formatting)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // Navigation Compose
    implementation(libs.androidx.navigation.compose)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    // Room DB
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // DataStore
    implementation(libs.androidx.datastore.preferences)

    // Testing
    testImplementation("org.json:json:20240303")

    // Compose UI Testing
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.test.manifest)

    // JUnit Runner
    androidTestImplementation(libs.androidx.junit)
    testImplementation(kotlin("test"))
}
