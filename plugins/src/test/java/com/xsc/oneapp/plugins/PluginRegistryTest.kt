package com.xsc.oneapp.plugins

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

private data class TestPlugin(override val pluginId: String, val label: String) : FeaturePlugin

class PluginRegistryTest {

    @Test
    fun `looks a registered plugin up by its id`() {
        val chat = TestPlugin("chat", "Chat")
        val registry = PluginRegistry(setOf(chat, TestPlugin("wallet", "Digital Wallet")))

        assertEquals(chat, registry["chat"])
        assertTrue(registry.contains("chat"))
    }

    @Test
    fun `returns null for an unregistered id`() {
        val registry = PluginRegistry(setOf(TestPlugin("chat", "Chat")))

        assertNull(registry["wallet"])
        assertFalse(registry.contains("wallet"))
    }

    @Test
    fun `all exposes every registered plugin`() {
        val registry = PluginRegistry(
            setOf(TestPlugin("chat", "Chat"), TestPlugin("wallet", "Digital Wallet"))
        )

        assertEquals(setOf("chat", "wallet"), registry.all.map { it.pluginId }.toSet())
    }

    @Test
    fun `refuses to construct with a duplicate pluginId rather than silently dropping one`() {
        assertThrows(IllegalStateException::class.java) {
            PluginRegistry(setOf(TestPlugin("chat", "Chat v1"), TestPlugin("chat", "Chat v2")))
        }
    }
}
