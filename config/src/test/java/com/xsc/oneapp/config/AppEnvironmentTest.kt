package com.xsc.oneapp.config

import org.junit.Assert.assertEquals
import org.junit.Test

class AppEnvironmentTest {

    @Test
    fun `fromWire parses a known value case-insensitively`() {
        assertEquals(AppEnvironment.PRODUCTION, AppEnvironment.fromWire("production"))
        assertEquals(AppEnvironment.UAT, AppEnvironment.fromWire("UAT"))
    }

    @Test
    fun `fromWire falls back to the default for null or unrecognized input`() {
        assertEquals(AppEnvironment.DEFAULT, AppEnvironment.fromWire(null))
        assertEquals(AppEnvironment.DEFAULT, AppEnvironment.fromWire("staging-typo"))
    }
}
