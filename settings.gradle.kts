pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Invence"
include(":app")
include(":data")
include(":domain")
include(":feature")
include(":libraries")
include(":data:product")
include(":domain:product")
include(":libraries:firebase")
include(":libraries:core")
include(":libraries:core-ui")
include(":feature:auth")
include(":feature:inventory")
include(":domain:user")
include(":data:user")
include(":feature:category")
include(":feature:product")
include(":data:branch")
include(":data:company")
include(":domain:company")
include(":domain:branch")
include(":feature:onboarding")
include(":feature:company")
include(":feature:barcode")
