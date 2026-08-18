package com.xsc.oneapp.branding

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Today's actual branding, reproduced as data rather than compiled-in constants -
 * this is not a placeholder or an invented default, every value below is the real
 * hex/string this app already ships (see the source cited on each field). A tenant-
 * aware/remote implementation of [BrandingRepository] can replace this binding
 * (di/BrandingModule.kt) without any consumer changing.
 */
@Singleton
class DefaultBrandingProvider @Inject constructor() : BrandingRepository {

    override suspend fun getBrandingConfig(): BrandingConfig = SINGLE_TENANT_CONFIG

    private companion object {
        /**
         * Source of every value: `sdk/XscThemeSDK/src/main/java/com/xsc/sdk/theme/Color.kt`
         * (OneAppPrimaryLight etc.) and `app/src/main/AndroidManifest.xml`'s app label
         * / `app/build.gradle.kts`'s `applicationId`. `tenantId` is `"default"` because
         * this app has exactly one tenant today - there is no multi-tenant contract yet
         * to source a real tenant id from (see docs/BACKEND_ENDPOINT_REQUIREMENTS.md).
         */
        val SINGLE_TENANT_CONFIG = BrandingConfig(
            tenantId = "default",
            appName = "OneApp",
            logoUrl = null,
            primaryColorLight = "#FF4F46E5",
            secondaryColorLight = "#FF06B6D4",
            tertiaryColorLight = "#FF8B5CF6",
            primaryColorDark = "#FF818CF8",
            secondaryColorDark = "#FF22D3EE",
            tertiaryColorDark = "#FFA78BFA",
            supportEmail = null,
            supportPhone = null
        )
    }
}
