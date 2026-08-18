plugins {
    id("oneapp.android.library")
    id("oneapp.android.hilt")
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.xsc.oneapp.core.permissions"

    buildFeatures {
        compose = true
    }
}

dependencies {
    // The real, existing permission source of truth (SessionManager.hasPermission,
    // backed by the signed-in user's JWT `permissions` claim). This module does not
    // reimplement RBAC - it centralizes *how the rest of the app asks the question*
    // behind one interface (PermissionChecker) instead of every feature importing
    // SessionManager directly, so a future change to where permissions come from
    // (e.g. a dedicated permission-sync endpoint) has one call site to update.
    implementation(project(":sdk:XscAuthSDK"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlinx.coroutines.core)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.hilt.navigation.compose)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
}
