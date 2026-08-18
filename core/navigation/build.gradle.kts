plugins {
    id("oneapp.android.library")
    id("oneapp.android.hilt")
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.xsc.oneapp.core.navigation"

    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(project(":core:permissions"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.play.feature.delivery)
    implementation(libs.play.feature.delivery.ktx)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
}
