package com.xsc.oneapp

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xsc.oneapp.core.navigation.NavigationRegistry
import com.xsc.sdk.auth.SessionManager
import com.xsc.sdk.network.APIClient
import com.xsc.sdk.network.APIError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Expected response structure from `user:view`. Adjust if the actual backend
 * response differs.
 */
data class UserRolesResponseDto(
    val permissions: List<String>?
)

@HiltViewModel
class MainViewModel @Inject constructor(
    val sessionManager: SessionManager,
    // Real, DI-collected feature route registry - see RootNavHost.kt's use in
    // Routes.destinationFor. Reached the same way sessionManager already is here:
    // via hiltViewModel() from a Composable with no ViewModel of its own, rather than
    // introducing an EntryPointAccessors-based lookup alongside this one.
    val navigationRegistry: NavigationRegistry,
    private val apiClient: APIClient,
) : ViewModel() {
    private val _isDarkMode = MutableStateFlow(value = false)
    val isDarkMode = _isDarkMode.asStateFlow()

    fun toggleTheme() {
        _isDarkMode.value = !_isDarkMode.value
    }

    /**
     * Called when the app transitions to the foreground.
     * Fetches the latest permissions from the backend and updates the session manager,
     * ensuring any admin permission changes apply immediately without a full logout.
     */
    fun refreshPermissions() {
        // Don't attempt to fetch permissions if not authenticated at all
        if (!sessionManager.isAuthenticated.value) return

        viewModelScope.launch {
            try {
                val response = apiClient.request<UserRolesResponseDto>(
                    module = "m_AAA",
                    submodule = "sm_auth",
                    action = "user:view",
                    actionType = "VIEW"
                )
                
                response?.permissions?.let { freshPermissions ->
                    sessionManager.updatePermissions(freshPermissions)
                    Log.d("MainViewModel", "Permissions refreshed from background: ${freshPermissions.size}")
                }
            } catch (e: APIError) {
                // Silently swallow network/business errors here: if the device is offline
                // when returning to the foreground, we want the app to gracefully fall
                // back to the existing JWT permissions rather than crashing the session.
                Log.e("MainViewModel", "Failed to refresh permissions on foreground", e)
            }
        }
    }
}
