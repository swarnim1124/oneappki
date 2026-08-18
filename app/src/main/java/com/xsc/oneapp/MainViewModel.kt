package com.xsc.oneapp

import androidx.lifecycle.ViewModel
import com.xsc.oneapp.core.navigation.NavigationRegistry
import com.xsc.sdk.auth.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val sessionManager: SessionManager,
    // Real, DI-collected feature route registry - see RootNavHost.kt's use in
    // Routes.destinationFor. Reached the same way sessionManager already is here:
    // via hiltViewModel() from a Composable with no ViewModel of its own, rather than
    // introducing an EntryPointAccessors-based lookup alongside this one.
    val navigationRegistry: NavigationRegistry,
) : ViewModel() {
    private val _isDarkMode = MutableStateFlow(value = false)
    val isDarkMode = _isDarkMode.asStateFlow()

    fun toggleTheme() {
        _isDarkMode.value = !_isDarkMode.value
    }
}
