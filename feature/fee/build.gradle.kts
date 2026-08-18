plugins {
    id("oneapp.android.library")
    id("oneapp.android.hilt")
    alias(libs.plugins.kotlin.compose)
}

// Razorpay's publishable key. It is a *public* identifier - it appears in the
// Checkout sheet and in every gateway request the client makes - so shipping it in
// BuildConfig is correct and is what Razorpay's own integration guide does. The
// secret key is a different value and must never leave the backend.
//
// Overridable per build (`-PrazorpayKeyId=rzp_live_...` or RAZORPAY_KEY_ID in the
// environment) so a release build does not need a source edit to stop pointing at
// the test account.
val razorpayKeyId: String = (project.findProperty("razorpayKeyId") as String?)
    ?.takeIf { it.isNotBlank() }
    ?: System.getenv("RAZORPAY_KEY_ID")?.takeIf { it.isNotBlank() }
    ?: "rzp_test_TItDGhLOVPL4Cu"

android {
    namespace = "com.xsc.oneapp.feature.fee"

    defaultConfig {
        buildConfigField("String", "VERSION_NAME", "\"1.0\"")
        buildConfigField("String", "RAZORPAY_KEY_ID", "\"$razorpayKeyId\"")
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(project(":core"))
    implementation(project(":core:navigation"))
    implementation(project(":sdk:XscNetworkSDK"))
    implementation(project(":sdk:XscAuthSDK"))
    implementation(project(":sdk:XscThemeSDK"))
    implementation(project(":sdk:XscCommonUI"))

    implementation(libs.androidx.core.ktx)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.gson)
    // Razorpay Checkout. `api` is deliberate-not-needed here: nothing outside this
    // module touches the SDK, the whole gateway surface stays behind
    // RazorpayCheckoutContract.
    implementation(libs.razorpay.checkout)
    implementation(libs.androidx.activity.compose)

    implementation(libs.hilt.navigation.compose)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
}
