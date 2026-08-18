package com.xsc.sdk.network

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class NetworkConfigTest {

    @Test
    fun `blank input yields no pins, leaving certificate pinning disabled`() {
        assertTrue(NetworkConfig.parseCertificatePins("").isEmpty())
    }

    @Test
    fun `parses a single host with multiple pins`() {
        val result = NetworkConfig.parseCertificatePins(
            "api.globaloneapp.com|sha256/AAAA=,sha256/BBBB="
        )

        assertEquals(
            mapOf("api.globaloneapp.com" to listOf("sha256/AAAA=", "sha256/BBBB=")),
            result
        )
    }

    @Test
    fun `parses multiple hosts separated by semicolons`() {
        val result = NetworkConfig.parseCertificatePins(
            "api.globaloneapp.com|sha256/AAAA=;staging.globaloneapp.com|sha256/CCCC="
        )

        assertEquals(
            mapOf(
                "api.globaloneapp.com" to listOf("sha256/AAAA="),
                "staging.globaloneapp.com" to listOf("sha256/CCCC=")
            ),
            result
        )
    }

    @Test
    fun `a malformed entry with no pins is dropped rather than crashing the app`() {
        val result = NetworkConfig.parseCertificatePins("api.globaloneapp.com|;also-bad")

        assertTrue(result.isEmpty())
    }
}
