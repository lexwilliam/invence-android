plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
    id("com.google.firebase.crashlytics")
}

android {
    namespace = "com.lexwilliam.invence"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.lexwilliam.invence"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(project(":data:product"))
    implementation(project(":domain:product"))
    implementation(project(":feature:auth"))
    implementation(project(":feature:inventory"))
    implementation(project(":libraries:firebase"))
    implementation(project(":libraries:core"))
    implementation(project(":libraries:core-ui"))

    // Hilt
    implementation(libs.hilt.library)
    implementation(libs.hilt.nav)
    kapt(libs.hilt.kapt)

    testImplementation(libs.bundles.unit.test.dependencies)
    androidTestImplementation(libs.bundles.integration.test.dependencies)
}