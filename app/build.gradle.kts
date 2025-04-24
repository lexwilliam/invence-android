import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

plugins {
    id("com.google.devtools.ksp")
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("com.google.dagger.hilt.android")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    alias(libs.plugins.kotlin.compose)
    id("org.jlleitschuh.gradle.ktlint") version "12.0.3"
}

android {
    namespace = "com.lexwilliam.invence"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.lexwilliam.invence"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
    buildFeatures {
        compose = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
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
    implementation(project(":data:branch"))
    implementation(project(":data:company"))
    implementation(project(":data:product"))
    implementation(project(":data:user"))
    implementation(project(":domain:branch"))
    implementation(project(":domain:company"))
    implementation(project(":domain:product"))
    implementation(project(":domain:user"))
    implementation(project(":feature:auth"))
    implementation(project(":feature:barcode"))
    implementation(project(":feature:category"))
    implementation(project(":feature:company"))
    implementation(project(":feature:inventory"))
    implementation(project(":feature:onboarding"))
    implementation(project(":feature:order"))
    implementation(project(":feature:product"))
    implementation(project(":feature:transaction"))
    implementation(project(":feature:profile"))
    implementation(project(":feature:analytics"))
    implementation(project(":libraries:core"))
    implementation(project(":libraries:core-ui"))
    implementation(project(":libraries:firebase"))

    // Hilt
    implementation(libs.hilt.library)
    implementation(libs.hilt.nav)
    implementation(libs.material3.android)
    ksp(libs.hilt.kapt)

    implementation(libs.google.play.service.auth)

    testImplementation(libs.bundles.unit.test.dependencies)
    androidTestImplementation(libs.bundles.integration.test.dependencies)
}