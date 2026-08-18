package com.xsc.oneapp.branding

/**
 * Source of the current tenant's [BrandingConfig]. [DefaultBrandingProvider] is the
 * only implementation today, returning this app's real current (single-tenant, build-
 * time) branding - see its kdoc. A remote-config-backed implementation (downloaded at
 * runtime per §13's "Configuration Download" flow, cached via DataStore) is Phase 3;
 * this interface is the real integration point that implementation will bind to,
 * with no change needed at any call site.
 */
interface BrandingRepository {
    suspend fun getBrandingConfig(): BrandingConfig
}
