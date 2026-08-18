package com.xsc.oneapp.branding

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class DefaultBrandingProviderTest {

    @Test
    fun `returns the app's real current brand palette, not a placeholder`() = runTest {
        val config = DefaultBrandingProvider().getBrandingConfig()

        // Matches sdk/XscThemeSDK/Color.kt's OneAppPrimaryLight/Dark exactly - see
        // DefaultBrandingProviderTest sibling assertions for the rest.
        assertEquals("#FF4F46E5", config.primaryColorLight)
        assertEquals("#FF818CF8", config.primaryColorDark)
        assertEquals("OneApp", config.appName)
        assertEquals("default", config.tenantId)
    }

    @Test
    fun `is stable across calls`() = runTest {
        val provider = DefaultBrandingProvider()

        assertEquals(provider.getBrandingConfig(), provider.getBrandingConfig())
    }
}
