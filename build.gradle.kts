// Root build.gradle.kts
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt.android) apply false
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.sonarqube)
}
sonar {
    properties {
        property("sonar.projectKey", "YourOrgKey_ExpenseLite")
        property("sonar.projectName", "ExpenseLite")
        property("sonar.organization", "amehran")
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.exclusions", "**/build/**, **/*.png, **/*.jpg")
    }
}