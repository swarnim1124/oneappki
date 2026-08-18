package com.xsc.oneapp.config

/**
 * Which product variant this build ships (architecture spec §29 - generic/demo/
 * internal build flavors). No Gradle product flavor exists yet for this today (the
 * app has exactly one `applicationId`, `com.xsc.oneapp` - see `app/build.gradle.kts`);
 * this is the typed model a future `flavorDimensions`/`productFlavors` block would
 * expose to app code, not a claim that flavors already exist.
 */
enum class BuildFlavor {
    /** The only flavor this app actually ships today. */
    GENERIC,
    DEMO,
    INTERNAL;

    companion object {
        val DEFAULT = GENERIC
    }
}
