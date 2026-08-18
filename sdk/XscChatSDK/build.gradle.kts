plugins {
    id("oneapp.android.library")
    id("oneapp.android.hilt")
}

android {
    namespace = "com.xsc.sdk.chat"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
}
