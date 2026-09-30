// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    id("org.jlleitschuh.gradle.ktlint") version "12.1.0" apply false // 13.x requiere java superior a 11, usamos 12.x estable
    id("io.gitlab.arturbosch.detekt") version "1.23.8" apply false
}