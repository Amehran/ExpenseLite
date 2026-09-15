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
        property("sonar.coverage.exclusions", "**/presentation/**, **/navigation/**, **/*Screen.kt, **/*Shell.kt, **/*Activity.kt, **/*Preview*.kt, **/di/**, **/data/local/entity/**, **/domain/model/**, **/DefaultData.kt")
        property("sonar.coverage.jacoco.xmlReportPaths", "${project.rootDir}/app/build/reports/jacoco/jacocoTestReport/jacocoTestReport.xml")
    }
}