import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.android.lib.plugin)
    alias(libs.plugins.kotlin.plugin)
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    id("org.jlleitschuh.gradle.ktlint") version "12.0.3"
    id("org.jetbrains.kotlin.plugin.serialization")
}

android {
    namespace = "com.lexwilliam.user"
    compileSdk = 35

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
        multiDexEnabled = true
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
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
    implementation(project(":libraries:core"))

    implementation(libs.kotlinx.serialization.json)

    implementation(libs.hilt.library)
    ksp(libs.hilt.kapt)

    testImplementation(libs.test.junit.unit)
    androidTestImplementation(libs.test.junit.android)
    androidTestImplementation(libs.test.espresso.core)
}