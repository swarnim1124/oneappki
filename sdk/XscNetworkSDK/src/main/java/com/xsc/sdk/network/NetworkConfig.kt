package com.xsc.sdk.network

/**
 * Single place to point the app at a backend. All OneApp modules (m_AAA, m_profile,
 * m_exam, m_curriculum, m_attendance, ...) share one dispatcher endpoint: `POST /`
 * on this base URL, routed internally by the `mod`/`subMod`/`action` envelope
 * fields - see the *_API_CONTRACT.md documents.
 *
 * The m_profile contract's "local default" (http://127.0.0.1:8000) is for running
 * that module against a bare local backend during isolated development; the
 * deployed environment referenced by m_AAA/m_exam/m_attendance is used here since
 * that's what every module contract calls the actual current deployment.
 *
 * BASE_URL is generated into BuildConfig from sdk/XscNetworkSDK/build.gradle.kts, so
 * it can be swapped per environment at build time (`-PbaseUrl=https://...`) instead
 * of editing source.
 */
object NetworkConfig {
    val BASE_URL: String = BuildConfig.BASE_URL

    /**
     * SHA-256 pins for [okhttp3.CertificatePinner], keyed by hostname - e.g.
     * `"api.globaloneapp.com" to listOf("sha256/AAAA...=")`. Parsed from
     * [BuildConfig.CERTIFICATE_PINS_RAW], which is empty unless
     * `-PcertificatePins=...` was passed at build time (see
     * sdk/XscNetworkSDK/build.gradle.kts for the format) - there is deliberately no
     * hardcoded default here: only whoever controls the production TLS certificate
     * can supply its real pins, and a wrong or made-up pin doesn't warn, it just
     * makes the app refuse to connect. NetworkModule only enables pinning when this
     * is non-empty, so leaving it unset preserves today's (unpinned) behavior exactly.
     */
    val CERTIFICATE_PINS: Map<String, List<String>> = parseCertificatePins(BuildConfig.CERTIFICATE_PINS_RAW)

    /** Internal (not private) so it's directly unit-testable without going through
     * BuildConfig, which is fixed at compile time. */
    internal fun parseCertificatePins(raw: String): Map<String, List<String>> {
        if (raw.isBlank()) return emptyMap()
        return raw.split(";")
            .mapNotNull { entry ->
                val parts = entry.split("|", limit = 2)
                val host = parts.getOrNull(0)?.trim()
                val pins = parts.getOrNull(1)?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() }
                if (host.isNullOrEmpty() || pins.isNullOrEmpty()) null else host to pins
            }
            .toMap()
    }

    /**
     * OkHttp timeouts, in seconds.
     *
     * Values are unchanged from the magic numbers previously inlined in NetworkModule -
     * naming them here only makes them reviewable. Worth revisiting: a 30-second connect
     * timeout means a user on a dead network waits half a minute before the app admits
     * it is offline, where 10s is the usual ceiling for "can I reach the host at all".
     * Changing it would alter perceived behaviour, so it is left alone here and raised
     * in the audit report instead.
     */
    const val CONNECT_TIMEOUT_SECONDS = 30L
    const val READ_TIMEOUT_SECONDS = 30L
    const val WRITE_TIMEOUT_SECONDS = 30L
}
