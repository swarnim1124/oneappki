plugins {
    id("oneapp.android.dynamic.feature")
    id("oneapp.android.hilt")
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.xsc.oneapp.feature.exam"

    defaultConfig {
        buildConfigField("String", "VERSION_NAME", "\"1.0\"")
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(project(":app"))
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

    implementation(libs.hilt.navigation.compose)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
}
