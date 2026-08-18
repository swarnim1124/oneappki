import java.util.Properties

plugins {
    id("oneapp.android.application")
    id("oneapp.android.hilt")
    alias(libs.plugins.kotlin.compose)

    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics.plugin)
    alias(libs.plugins.firebase.perf.plugin)
}

// Release signing material, read from a gitignored `keystore.properties` at the repo
// root (see keystore.properties.example) or from environment variables of the same
// name, uppercased and prefixed RELEASE_ - whichever CI already prefers. Neither the
// keystore file nor any of these values are ever committed: `*.keystore`,
// `keystore.properties` and `signing.properties` were already in .gitignore, but
// nothing actually read them - `config/signing.gradle.kts` was a dead stub nobody
// applied (see PRODUCTION_READINESS_AUDIT.md C-2), so `assembleRelease` produced an
// unsignable APK.
val keystoreProperties = Properties().apply {
    val propsFile = rootProject.file("keystore.properties")
    if (propsFile.exists()) {
        propsFile.inputStream().use { load(it) }
    }
}

fun releaseSigningValue(propertyKey: String, envKey: String): String? =
    keystoreProperties.getProperty(propertyKey)?.takeIf { it.isNotBlank() }
        ?: System.getenv(envKey)?.takeIf { it.isNotBlank() }

val releaseStoreFilePath = releaseSigningValue("storeFile", "RELEASE_KEYSTORE_PATH")
val releaseStorePassword = releaseSigningValue("storePassword", "RELEASE_KEYSTORE_PASSWORD")
val releaseKeyAlias = releaseSigningValue("keyAlias", "RELEASE_KEY_ALIAS")
val releaseKeyPassword = releaseSigningValue("keyPassword", "RELEASE_KEY_PASSWORD")

// Only true once every credential is actually supplied. Without this, `assembleRelease`
// still works (unsigned, same as before) instead of failing every dev/CI machine that
// hasn't set up real release signing yet - only `bundleRelease`/a real upload needs it.
val hasReleaseSigningConfig = !releaseStoreFilePath.isNullOrBlank() &&
    !releaseStorePassword.isNullOrBlank() &&
    !releaseKeyAlias.isNullOrBlank() &&
    !releaseKeyPassword.isNullOrBlank()

android {
    namespace = "com.xsc.oneapp"

    dynamicFeatures += listOf(
        ":feature:exam",
        ":feature:fee",
        ":feature:attendance",
        ":feature:curriculum",
        ":feature:timetable",
        ":feature:profile"
    )

    defaultConfig {
        applicationId = "com.xsc.oneapp"
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    if (hasReleaseSigningConfig) {
        signingConfigs {
            create("release") {
                storeFile = rootProject.file(releaseStoreFilePath!!)
                storePassword = releaseStorePassword
                keyAlias = releaseKeyAlias
                keyPassword = releaseKeyPassword
            }
        }
    }

    buildTypes {
        release {
            // R8 was previously disabled entirely (isMinifyEnabled = false), which
            // shipped an unshrunk, unobfuscated APK: the whole class hierarchy, the
            // dispatcher envelope contract and every endpoint constant were readable
            // straight out of the APK. See proguard-rules.pro for the Gson/Retrofit
            // keep rules this requires - Gson is reflection-based, so R8 renaming a
            // DTO field silently turns it into a null instead of a build failure.
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            if (hasReleaseSigningConfig) {
                signingConfig = signingConfigs.getByName("release")
            }
            // Else: falls back to AGP's default (unsigned) - same as before this
            // change - until keystore.properties or the RELEASE_* env vars are set.
        }
        debug {
            // Explicit and unminified so a debug build is never accidentally shrunk
            // while iterating on keep rules.
            isMinifyEnabled = false
        }
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    // Needed directly (not just transitively through the feature modules) for
    // CrashReporter.init() in OneAppApplication.
    implementation(project(":core"))
    // NavigationRegistry (Routes.destinationFor's fallback resolver) and the
    // PermissionGate this app gates Timetable's route with - see RootNavHost.kt.
    implementation(project(":core:navigation"))
    implementation(project(":core:permissions"))
    implementation(project(":feature:login"))
    implementation(project(":feature:dashboard"))
    implementation(project(":feature:profile"))
    implementation(project(":feature:exam"))
    implementation(project(":feature:attendance"))
    implementation(project(":feature:curriculum"))
    implementation(project(":feature:timetable"))
    implementation(project(":feature:fee"))
    implementation(project(":sdk:XscAuthSDK"))
    implementation(project(":sdk:XscThemeSDK"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.hilt.navigation.compose)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
