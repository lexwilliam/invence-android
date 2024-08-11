pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
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
include(":feature:order")
include(":feature:home")
include(":data:order")
include(":domain:order")
include(":data:transaction")
include(":domain:transaction")
include(":feature:transaction")
include(":libraries:db")
include(":data:log")
include(":domain:log")
include(":feature:profile")
