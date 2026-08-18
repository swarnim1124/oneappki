package com.xsc.oneapp.core.navigation

import android.content.Context
import com.google.android.play.core.splitinstall.SplitInstallManager
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.google.android.play.core.splitinstall.SplitInstallRequest
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

sealed class ModuleInstallStatus {
    object NotInstalled : ModuleInstallStatus()
    data class Downloading(val progress: Float) : ModuleInstallStatus()
    object Installing : ModuleInstallStatus()
    object Installed : ModuleInstallStatus()
    data class Failed(val error: String) : ModuleInstallStatus()
}

@Singleton
class DynamicModuleLoader @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val splitInstallManager: SplitInstallManager by lazy {
        SplitInstallManagerFactory.create(context)
    }

    private val _installStatus = MutableStateFlow<Map<String, ModuleInstallStatus>>(emptyMap())
    val installStatus: StateFlow<Map<String, ModuleInstallStatus>> = _installStatus.asStateFlow()

    fun isModuleInstalled(moduleName: String): Boolean {
        return splitInstallManager.installedModules.contains(moduleName)
    }

    fun installModule(moduleName: String, onComplete: (Boolean) -> Unit) {
        if (isModuleInstalled(moduleName)) {
            updateStatus(moduleName, ModuleInstallStatus.Installed)
            onComplete(true)
            return
        }

        updateStatus(moduleName, ModuleInstallStatus.Downloading(0f))

        val request = SplitInstallRequest.newBuilder()
            .addModule(moduleName)
            .build()

        val listener = object : SplitInstallStateUpdatedListener {
            override fun onStateUpdate(state: com.google.android.play.core.splitinstall.SplitInstallSessionState) {
                if (state.moduleNames().contains(moduleName)) {
                    when (state.status()) {
                        SplitInstallSessionStatus.DOWNLOADING -> {
                            val total = state.totalBytesToDownload().toFloat().coerceAtLeast(1f)
                            val downloaded = state.bytesDownloaded().toFloat()
                            updateStatus(moduleName, ModuleInstallStatus.Downloading(downloaded / total))
                        }
                        SplitInstallSessionStatus.INSTALLING -> {
                            updateStatus(moduleName, ModuleInstallStatus.Installing)
                        }
                        SplitInstallSessionStatus.INSTALLED -> {
                            updateStatus(moduleName, ModuleInstallStatus.Installed)
                            splitInstallManager.unregisterListener(this)
                            onComplete(true)
                        }
                        SplitInstallSessionStatus.FAILED -> {
                            updateStatus(moduleName, ModuleInstallStatus.Failed("Install failed with error code: ${state.errorCode()}"))
                            splitInstallManager.unregisterListener(this)
                            onComplete(false)
                        }
                        SplitInstallSessionStatus.CANCELED -> {
                            updateStatus(moduleName, ModuleInstallStatus.Failed("Installation canceled"))
                            splitInstallManager.unregisterListener(this)
                            onComplete(false)
                        }
                        else -> {}
                    }
                }
            }
        }

        splitInstallManager.registerListener(listener)

        splitInstallManager.startInstall(request)
            .addOnFailureListener { exception ->
                updateStatus(moduleName, ModuleInstallStatus.Failed(exception.localizedMessage ?: "Failed to launch dynamic install"))
                onComplete(false)
            }
    }

    private fun updateStatus(moduleName: String, status: ModuleInstallStatus) {
        val current = _installStatus.value.toMutableMap()
        current[moduleName] = status
        _installStatus.value = current
    }
}
