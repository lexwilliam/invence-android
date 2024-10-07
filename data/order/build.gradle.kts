import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.android.lib.plugin)
    alias(libs.plugins.kotlin.plugin)
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
    id("io.realm.kotlin")
    kotlin("plugin.serialization")
    id("org.jlleitschuh.gradle.ktlint") version "12.0.3"
}

android {
    namespace = "com.lexwilliam.order"
    compileSdk = 34

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

tasks.getByPath("preBuild").dependsOn("ktlintFormat")

ktlint {
    android = true
    reporters {
        reporter(ReporterType.PLAIN)
        reporter(ReporterType.CHECKSTYLE)
        reporter(ReporterType.SARIF)
    }
}

dependencies {
    api(project(":domain:order"))
    implementation(project(":libraries:db"))
    implementation(project(":libraries:core"))
    implementation(project(":libraries:firebase"))

    implementation(libs.hilt.library)
    kapt(libs.hilt.kapt)

    implementation(libs.kotlinx.serialization.json)

    testImplementation(libs.test.junit.unit)
    androidTestImplementation(libs.test.junit.android)
    androidTestImplementation(libs.test.espresso.core)
}