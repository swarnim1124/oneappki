package com.xsc.oneapp.config.model

import com.google.gson.annotations.SerializedName

/**
 * Root configuration object downloaded from the backend or cached locally in DataStore.
 */
data class RemoteAppConfig(
    @SerializedName("tenant_id") val tenantId: String = "default",
    @SerializedName("version") val version: Int = 1,
    @SerializedName("feature_flags") val featureFlags: Map<String, Boolean> = emptyMap(),
    @SerializedName("maintenance_mode") val maintenanceMode: MaintenanceModeConfig = MaintenanceModeConfig(),
    @SerializedName("navigation") val navigation: DynamicNavigationConfig = DynamicNavigationConfig(),
    @SerializedName("theme") val theme: DynamicThemeConfig = DynamicThemeConfig()
)

data class MaintenanceModeConfig(
    @SerializedName("is_active") val isActive: Boolean = false,
    @SerializedName("title") val title: String = "Under Maintenance",
    @SerializedName("message") val message: String = "OneApp is currently undergoing scheduled maintenance. Please check back later.",
    @SerializedName("allow_bypass") val allowBypass: Boolean = false
)

data class DynamicNavigationConfig(
    @SerializedName("enabled_modules") val enabledModules: List<String> = listOf(
        "dashboard", "profile", "exam", "attendance", "curriculum", "timetable", "fee"
    ),
    @SerializedName("default_route") val defaultRoute: String = "dashboard",
    @SerializedName("custom_routes") val customRoutes: Map<String, String> = emptyMap()
)

data class DynamicThemeConfig(
    @SerializedName("brand_name") val brandName: String = "OneApp",
    @SerializedName("primary_color_hex") val primaryColorHex: String = "#3F51B5",
    @SerializedName("secondary_color_hex") val secondaryColorHex: String = "#FF4081",
    @SerializedName("surface_color_hex") val surfaceColorHex: String = "#FAFAFA",
    @SerializedName("background_color_hex") val backgroundColorHex: String = "#FFFFFF",
    @SerializedName("is_dark_mode_supported") val isDarkModeSupported: Boolean = true,
    @SerializedName("logo_url") val logoUrl: String? = null
)
