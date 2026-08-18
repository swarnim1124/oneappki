plugins {
    id("oneapp.android.library")
    id("oneapp.android.hilt")
}

android {
    namespace = "com.xsc.oneapp.config"
}

dependencies {
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.gson)
    testImplementation(libs.junit)
}
