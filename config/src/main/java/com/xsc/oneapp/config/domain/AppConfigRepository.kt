package com.xsc.oneapp.config.domain

import com.xsc.oneapp.config.data.ConfigurationDataStore
import com.xsc.oneapp.config.model.DynamicNavigationConfig
import com.xsc.oneapp.config.model.DynamicThemeConfig
import com.xsc.oneapp.config.model.MaintenanceModeConfig
import com.xsc.oneapp.config.model.RemoteAppConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import javax.inject.Singleton

interface AppConfigRepository {
    val configState: StateFlow<RemoteAppConfig>
    fun isFeatureEnabled(flagName: String, defaultValue: Boolean = true): Boolean
    fun isModuleEnabled(moduleId: String): Boolean
    fun getMaintenanceMode(): MaintenanceModeConfig
    fun getDynamicThemeConfig(): DynamicThemeConfig
    fun getNavigationConfig(): DynamicNavigationConfig
    suspend fun updateConfig(newConfig: RemoteAppConfig)
}

@Singleton
class AppConfigRepositoryImpl @Inject constructor(
    private val dataStore: ConfigurationDataStore
) : AppConfigRepository {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override val configState: StateFlow<RemoteAppConfig> = dataStore.appConfigFlow
        .stateIn(
            scope = scope,
            started = SharingStarted.Eagerly,
            initialValue = RemoteAppConfig()
        )

    override fun isFeatureEnabled(flagName: String, defaultValue: Boolean): Boolean {
        return configState.value.featureFlags[flagName] ?: defaultValue
    }

    override fun isModuleEnabled(moduleId: String): Boolean {
        val enabledModules = configState.value.navigation.enabledModules
        return enabledModules.isEmpty() || enabledModules.contains(moduleId.lowercase())
    }

    override fun getMaintenanceMode(): MaintenanceModeConfig {
        return configState.value.maintenanceMode
    }

    override fun getDynamicThemeConfig(): DynamicThemeConfig {
        return configState.value.theme
    }

    override fun getNavigationConfig(): DynamicNavigationConfig {
        return configState.value.navigation
    }

    override suspend fun updateConfig(newConfig: RemoteAppConfig) {
        dataStore.saveAppConfig(newConfig)
    }
}
