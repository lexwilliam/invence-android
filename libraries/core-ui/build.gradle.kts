import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.android.lib.plugin)
    alias(libs.plugins.kotlin.plugin)
    id("org.jlleitschuh.gradle.ktlint") version "12.0.3"
}

android {
    namespace = "com.lexwilliam.core_ui"
    compileSdk = 34

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
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.7"
    }
    kotlinOptions.languageVersion = "1.9"
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
    api(libs.bundles.android.ui.dependencies)
    api(platform(libs.compose.bom))
    api(libs.bundles.compose.dependencies)

    implementation(libs.kotlinx.datetime)
    implementation(libs.coil.library)
    api(libs.hilt.nav)

    implementation(libs.accompanist.permissions)
    implementation(libs.bundles.camera.dependencies)
    implementation(libs.mlkit.barcode.scanning)

    testImplementation(libs.bundles.unit.test.dependencies)
    androidTestImplementation(libs.bundles.integration.test.dependencies)
    debugApi(libs.compose.tooling)
}