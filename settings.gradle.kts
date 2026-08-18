pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "OneApp"

include(":app")
include(":core")
// Added under core/ as sibling Gradle project paths rather than moving :core's
// existing content - 8 feature modules already depend on `project(":core")`, and with
// no Android/Gradle build available in this environment to verify a refactor of that
// module against every dependent, adding new nested modules is the change that can't
// silently break what already compiles. See docs/PRODUCTION_READINESS.md Risk #5 for
// why :core:permissions exists and app/navigation/Routes.kt for :core:navigation.
include(":core:permissions")
include(":core:navigation")

// White-label branding, environment/build-flavor configuration and a generic
// feature-plugin registry (architecture audit Phase 2 - PDF "Configuration Engine" /
// "White Label Config" / future-module plugin architecture). Real starter
// infrastructure, not yet wired into runtime theme selection or a remote config
// download - that's Phase 3 (docs/PRODUCTION_READINESS.md / the audit report).
include(":branding")
include(":config")
include(":plugins")

include(":sdk:XscNetworkSDK")
include(":sdk:XscAuthSDK")
include(":sdk:XscThemeSDK")
include(":sdk:XscCommonUI")
include(":sdk:XscQRCodeSDK")
include(":sdk:XscMediaSDK")
include(":sdk:XscCameraSDK")
include(":sdk:XscFileSDK")
include(":sdk:XscChatSDK")

include(":feature:login")
include(":feature:dashboard")
include(":feature:profile")
include(":feature:exam")
include(":feature:attendance")
include(":feature:curriculum")
include(":feature:timetable")
include(":feature:fee")
