package com.xsc.oneapp.config

/**
 * Typed form of the deployment target this build talks to (architecture spec §29 -
 * "Environment Config" / build variants: development/qa/uat/production).
 *
 * This does not yet replace `sdk/XscNetworkSDK/build.gradle.kts`'s existing
 * `-PbaseUrl=...` Gradle-property mechanism, which is real, tested and already
 * refuses to build a release variant against the default dev URL (see
 * `NetworkConfig.BASE_URL`, `docs/PRODUCTION_READINESS.md` C-5) - rewiring that to
 * read from this type instead is a natural follow-up, not done here, since it means
 * touching the one file every environment-specific build already depends on with no
 * way to compile-verify the change (see docs/PRODUCTION_READINESS.md and the audit
 * report's build-verification constraints).
 */
enum class AppEnvironment {
    DEVELOPMENT,
    QA,
    UAT,
    PRODUCTION;

    companion object {
        /** Matches the current default in `sdk/XscNetworkSDK/build.gradle.kts`
         * (`https://dev.globaloneapp.com/` when `-PbaseUrl` isn't passed). */
        val DEFAULT = DEVELOPMENT

        /** Parses a build-property-style value (`"production"`, `"UAT"`), defaulting
         * to [DEFAULT] for anything unrecognized rather than failing the build - a
         * typo in an environment name should not be able to silently mean "assume
         * dev," but nor should it be able to crash a build over a config module with
         * no consumer yet; once this drives real build config selection, an unknown
         * value should fail loudly instead (tracked as a Phase 3 follow-up, not
         * relevant while this type has no wiring to affect a running build). */
        fun fromWire(raw: String?): AppEnvironment =
            entries.firstOrNull { it.name.equals(raw, ignoreCase = true) } ?: DEFAULT
    }
}
