// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    dependencies {
        classpath(libs.gms)
        classpath(libs.firebase.crashlytics.gradle)
        classpath(libs.ktlint.gradle)
    }
}

plugins {
    kotlin("jvm") version "2.1.10"
    kotlin("plugin.serialization") version "2.1.10"
    id("com.google.devtools.ksp") version "2.1.10-1.0.31" apply false
    alias(libs.plugins.android.app.plugin) apply false
    alias(libs.plugins.android.lib.plugin) apply false
    alias(libs.plugins.kotlin.plugin) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.hilt.plugin) apply false
    id("org.jlleitschuh.gradle.ktlint") version "12.0.3"
}