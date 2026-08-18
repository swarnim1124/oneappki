plugins {
    id("oneapp.android.library")
    id("oneapp.android.hilt")
}

android {
    namespace = "com.xsc.sdk.media"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    api(libs.coil.compose)
}
