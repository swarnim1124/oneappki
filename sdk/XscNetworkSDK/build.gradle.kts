plugins {
    id("oneapp.android.library")
    id("oneapp.android.hilt")
    alias(libs.plugins.kotlin.serialization)
}

// Override at build time instead of editing source, e.g.:
//   ./gradlew assembleRelease -PbaseUrl=https://staging.globaloneapp.com/ -PrecaptchaSiteKey=...
// Defaults preserve today's behavior exactly for debug: the dev deployment every
// *_API_CONTRACT.md document references, and an empty reCAPTCHA key (safe no-op -
// see RecaptchaManager).
val explicitBaseUrl = project.findProperty("baseUrl") as String?
val baseUrl = explicitBaseUrl ?: "https://dev.globaloneapp.com/"
val recaptchaSiteKey = (project.findProperty("recaptchaSiteKey") as String?) ?: ""

// SHA-256 SPKI pins for OkHttp's CertificatePinner (parsed in NetworkConfig.CERTIFICATE_PINS,
// consumed by NetworkModule). Nothing hardcoded here - these are the production TLS
// certificate's own pins, which only whoever controls that certificate can supply;
// there is no safe placeholder to ship instead (a wrong pin doesn't warn, it just
// makes the app refuse to connect). Empty by default, so pinning stays off exactly
// as before until real pins are supplied.
// Format: "host1|sha256/AAAA=,sha256/BBBB=;host2|sha256/CCCC="
//   ./gradlew assembleRelease -PbaseUrl=... -PcertificatePins="api.globaloneapp.com|sha256/AAAA=,sha256/BBBB="
val certificatePins = (project.findProperty("certificatePins") as String?) ?: ""

// PRODUCTION_READINESS_AUDIT.md C-5: a plain `./gradlew assembleRelease` used to
// silently produce a release build still pointed at the dev server, because this
// default applied to every build type equally. Refuse to build a release variant
// unless `-PbaseUrl=...` (or CI's equivalent property injection) was passed
// explicitly - debug is untouched.
val isBuildingReleaseVariant = gradle.startParameter.taskNames.any { taskName ->
    taskName.contains("Release")
}
if (isBuildingReleaseVariant && explicitBaseUrl.isNullOrBlank()) {
    throw GradleException(
        "Refusing to build a release variant with the default dev base URL " +
            "($baseUrl). Pass -PbaseUrl=<the real staging/production URL> explicitly."
    )
}

android {
    namespace = "com.xsc.sdk.network"

    defaultConfig {
        buildConfigField("String", "BASE_URL", "\"$baseUrl\"")
        buildConfigField("String", "RECAPTCHA_SITE_KEY", "\"$recaptchaSiteKey\"")
        buildConfigField("String", "CERTIFICATE_PINS_RAW", "\"$certificatePins\"")
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(project(":sdk:XscAuthSDK"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.play.services)

    api(libs.retrofit.core)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp.core)
    implementation(libs.okhttp.logging.interceptor)
    api(libs.gson)
    api(libs.kotlinx.serialization.json)
    implementation(libs.retrofit.converter.kotlinx.serialization)

    implementation(libs.play.services.recaptcha)

    testImplementation(libs.junit)
}
