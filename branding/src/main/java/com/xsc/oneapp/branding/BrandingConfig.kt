package com.xsc.oneapp.branding

/**
 * White-label branding for one tenant (architecture spec §13 - "White Label Config").
 *
 * Every field here already exists as a hardcoded value somewhere in the app today
 * (see [DefaultBrandingProvider] for exactly where each one comes from) - this type
 * exists to give those values one real, tenant-scoped shape instead of being
 * scattered across `sdk/XscThemeSDK`'s Color.kt/Theme.kt and `app`'s manifest/string
 * resources, ahead of a real per-tenant/remote-driven source (architecture audit
 * Phase 3 - see docs/PRODUCTION_READINESS.md).
 *
 * Colors are ARGB hex strings (`"#FF4F46E5"`), not `androidx.compose.ui.graphics.Color`,
 * so this module - and anything that parses or transports a tenant's config - never
 * needs a Compose/Android UI dependency. `XscThemeSDK`'s `OneAppTheme` is what turns
 * these into an actual `ColorScheme`.
 */
data class BrandingConfig(
    /** Tenant/institution identifier this config belongs to. */
    val tenantId: String,
    val appName: String,
    val logoUrl: String?,
    val primaryColorLight: String,
    val secondaryColorLight: String,
    val tertiaryColorLight: String,
    val primaryColorDark: String,
    val secondaryColorDark: String,
    val tertiaryColorDark: String,
    /** Support/help contact shown on the Dashboard's Profile tab and similar. */
    val supportEmail: String?,
    val supportPhone: String?
)
