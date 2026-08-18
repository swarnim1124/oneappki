package com.xsc.oneapp.core.security

import javax.inject.Inject
import javax.inject.Singleton

data class SecurityAuditReport(
    val isRooted: Boolean,
    val isEmulator: Boolean,
    val isTampered: Boolean,
    val signatureHash: String?,
    val isDeviceSecure: Boolean
)

@Singleton
class SecurityAuditManager @Inject constructor(
    private val rootDetector: RootDetector,
    private val emulatorDetector: EmulatorDetector,
    private val tamperDetector: TamperDetector
) {
    fun performFullAudit(expectedSignatureHash: String? = null): SecurityAuditReport {
        val isRooted = rootDetector.isRooted()
        val isEmulator = emulatorDetector.isEmulator()
        val isTampered = tamperDetector.isAppTampered(expectedSignatureHash)
        val signatureHash = tamperDetector.getAppSignatureHash()

        val isDeviceSecure = !isRooted && !isTampered

        return SecurityAuditReport(
            isRooted = isRooted,
            isEmulator = isEmulator,
            isTampered = isTampered,
            signatureHash = signatureHash,
            isDeviceSecure = isDeviceSecure
        )
    }
}
