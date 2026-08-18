package com.xsc.oneapp.plugins

/**
 * Generic, typed holder for a Hilt-collected `Set<T : FeaturePlugin>` - construct one
 * per plugin contract (e.g. a hypothetical `PluginRegistry<ChatCapability>` built from
 * `Set<ChatCapability>>` the same way `core.navigation.NavigationRegistry` is built
 * from `Set<NavigationContribution>` today), rather than every future extension point
 * writing its own id-lookup/duplicate-handling code from scratch.
 *
 * Not itself a Hilt-injected type: a real, Hilt-collected `Set<T>` is *what you build
 * one of these from* (see the constructor), the same way `NavigationRegistry` wraps
 * its own injected Set rather than this class being injected generically - Hilt
 * cannot inject a generic `Set<T>` for an arbitrary `T` chosen at the call site.
 */
class PluginRegistry<T : FeaturePlugin>(plugins: Set<T>) {

    private val byId: Map<String, T> = plugins.associateBy { it.pluginId }

    init {
        check(byId.size == plugins.size) {
            val duplicates = plugins.groupBy { it.pluginId }.filterValues { it.size > 1 }.keys
            "Duplicate FeaturePlugin.pluginId(s) registered: $duplicates"
        }
    }

    val all: Collection<T> get() = byId.values

    operator fun get(pluginId: String): T? = byId[pluginId]

    fun contains(pluginId: String): Boolean = pluginId in byId
}
