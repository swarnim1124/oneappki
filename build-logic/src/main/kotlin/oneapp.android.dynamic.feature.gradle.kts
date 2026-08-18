import com.android.build.api.dsl.DynamicFeatureExtension
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

apply(plugin = "com.android.dynamic-feature")
apply(plugin = "org.jetbrains.kotlin.android")

configure<DynamicFeatureExtension> {
    compileSdk = 35

    defaultConfig {
        minSdk = 29
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_11)
    }
}
