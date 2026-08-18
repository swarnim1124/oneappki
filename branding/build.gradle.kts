plugins {
    id("oneapp.android.library")
    id("oneapp.android.hilt")
}

android {
    namespace = "com.xsc.oneapp.branding"
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}
