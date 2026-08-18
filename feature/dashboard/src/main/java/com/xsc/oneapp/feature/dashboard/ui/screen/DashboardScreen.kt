@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.xsc.oneapp.feature.dashboard.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.xsc.oneapp.feature.dashboard.domain.model.DashboardTab
import com.xsc.oneapp.feature.dashboard.ui.components.ActionsFeedSection
import com.xsc.oneapp.feature.dashboard.ui.components.BottomTabBar
import com.xsc.oneapp.feature.dashboard.ui.components.DashboardTopBar
import com.xsc.oneapp.feature.dashboard.ui.components.DayAtGlanceSection
import com.xsc.oneapp.feature.dashboard.ui.components.ModulesTab
import com.xsc.oneapp.feature.dashboard.ui.components.NotificationsList
import com.xsc.oneapp.feature.dashboard.ui.components.PinModulesDialog
import com.xsc.oneapp.feature.dashboard.ui.components.SectionHeader
import com.xsc.oneapp.feature.dashboard.ui.components.SidebarView
import com.xsc.oneapp.feature.dashboard.ui.components.TodayTimelineCard
import com.xsc.oneapp.feature.dashboard.ui.components.WorkspaceCard
import com.xsc.oneapp.feature.dashboard.ui.viewmodel.DashboardState
import com.xsc.oneapp.feature.dashboard.ui.viewmodel.DashboardViewModel
import com.xsc.sdk.theme.LocalDarkTheme
import com.xsc.sdk.theme.LocalSpacing
import com.xsc.sdk.theme.LocalThemeToggle
import com.xsc.sdk.theme.OneAppMotion

/**
 * Dashboard shell.
 *
 * Presentation only. Every ViewModel call, tab value, navigation callback and dialog
 * trigger is unchanged from the previous version - the differences are spacing, the type
 * scale, elevation hierarchy and motion.
 *
 * Home, the Pin Modules dialog and Notifications were rebuilt against the Stitch
 * redesign (see DashboardViewModel / HomeGlanceComponents.kt / NotificationsComponents.kt
 * for what's real vs. temporary). Curriculum and Profile are unchanged pending their
 * own Stitch screens.
 */
@Composable
fun DashboardScreen(
    onNavigateToModule: (String) -> Unit,
    onLogout: () -> Unit,
    onChangePassword: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val isDarkMode = LocalDarkTheme.current
    val toggleTheme = LocalThemeToggle.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            DashboardTopBar(
                onMenuTap = { viewModel.toggleSidebar() },
                unreadCount = state.unreadNotifications,
                onNotificationTap = { viewModel.setTab(DashboardTab.NOTIFICATIONS) },
                onProfileTap = { viewModel.setTab(DashboardTab.PROFILE) }
            )

            Box(modifier = Modifier.weight(1f)) {
                when (state.selectedTab) {
                    DashboardTab.HOME -> HomeTab(
                        state = state,
                        greeting = viewModel.getGreeting(),
                        roleSubtitle = viewModel.getRoleSubtitle(),
                        onNavigateToModule = onNavigateToModule,
                        onOpenPinPicker = { viewModel.openPinPicker() },
                        onSelectTab = { viewModel.setTab(it) }
                    )
                    DashboardTab.CURRICULUM -> ModulesTab(
                        state = state,
                        onNavigateToModule = onNavigateToModule
                    )
                    DashboardTab.NOTIFICATIONS -> NotificationsTab(
                        state = state,
                        onMarkAllRead = { viewModel.markAllNotificationsRead() }
                    )
                    DashboardTab.PROFILE -> ProfileTab(
                        state = state,
                        onSignOut = {
                            viewModel.logout()
                            onLogout()
                        },
                        onNavigateToModule = onNavigateToModule,
                        onChangePassword = onChangePassword,
                        onNotificationsClick = { viewModel.setTab(DashboardTab.NOTIFICATIONS) }
                    )
                }
            }

            BottomTabBar(
                selectedTab = state.selectedTab,
                onTabSelected = { viewModel.setTab(it) }
            )
        }

        // Scrim fades while the panel slides - the panel used to appear over an
        // untinted screen, so it read as a floating rectangle rather than a drawer.
        AnimatedVisibility(
            visible = state.isSidebarOpen,
            enter = slideInHorizontally(
                animationSpec = tween(OneAppMotion.DurationLong, easing = OneAppMotion.EmphasizedDecelerate),
                initialOffsetX = { -it }
            ) + fadeIn(tween(OneAppMotion.DurationShort)),
            exit = slideOutHorizontally(
                animationSpec = tween(OneAppMotion.DurationMedium, easing = OneAppMotion.EmphasizedAccelerate),
                targetOffsetX = { -it }
            ) + fadeOut(tween(OneAppMotion.DurationShort)),
            modifier = Modifier.zIndex(10f)
        ) {
            SidebarView(
                state = state,
                onClose = { viewModel.closeSidebar() },
                onModuleSelect = { module ->
                    viewModel.selectModule(module)
                    onNavigateToModule(module.route)
                },
                onLogout = {
                    viewModel.logout()
                    onLogout()
                },
                isDarkMode = isDarkMode,
                onThemeToggle = { toggleTheme() }
            )
        }

        if (state.isPinPickerOpen) {
            PinModulesDialog(
                allModules = state.modules,
                pinnedIds = state.pinnedModuleIds,
                atPinLimit = state.pinnedLimitReached,
                onToggle = { viewModel.togglePinnedModule(it) },
                onDismiss = { viewModel.closePinPicker() }
            )
        }
    }
}

@Composable
private fun HomeTab(
    state: DashboardState,
    greeting: String,
    roleSubtitle: String,
    onNavigateToModule: (String) -> Unit,
    onOpenPinPicker: () -> Unit,
    onSelectTab: (DashboardTab) -> Unit
) {
    val spacing = LocalSpacing.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(
                start = spacing.screenHorizontal,
                end = spacing.screenHorizontal,
                top = spacing.sm,
                bottom = spacing.xxl
            ),
        verticalArrangement = Arrangement.spacedBy(spacing.xl)
    ) {
        Column(modifier = Modifier.padding(vertical = spacing.sm)) {
            Text(
                text = "$greeting, ${state.userFirstName}",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(spacing.xs))
            Text(
                text = roleSubtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(spacing.md)) {
            SectionHeader(title = "Day at a Glance")
            DayAtGlanceSection(stats = state.stats)
            TodayTimelineCard(points = state.todayTimeline)
        }

        val feesStat = state.stats.firstOrNull { it.id == "fees" }

        Column(verticalArrangement = Arrangement.spacedBy(spacing.md)) {
            SectionHeader(title = "Your Workspace", trailing = "Edit", onTrailingClick = onOpenPinPicker)
            WorkspaceCard(
                modules = state.modules,
                pinnedIds = state.pinnedModuleIds,
                hasFeesAlert = feesStat?.tag == "Due now",
                onModuleClick = { onNavigateToModule(it.route) },
                onMoreClick = { onSelectTab(DashboardTab.CURRICULUM) }
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(spacing.md)) {
            SectionHeader(title = "Actions & Feed")
            ActionsFeedSection(
                feesStat = feesStat,
                recentActivity = state.recentActivity,
                onPayFees = { onNavigateToModule("/fees") },
                onViewAllActivity = { onSelectTab(DashboardTab.NOTIFICATIONS) }
            )
        }
    }
}

@Composable
private fun NotificationsTab(state: DashboardState, onMarkAllRead: () -> Unit) {
    NotificationsList(notifications = state.notifications, onMarkAllRead = onMarkAllRead)
}

@Composable
private fun ProfileTab(
    state: DashboardState,
    onSignOut: () -> Unit,
    onNavigateToModule: (String) -> Unit,
    onChangePassword: () -> Unit,
    onNotificationsClick: () -> Unit
) {
    val spacing = LocalSpacing.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = spacing.screenHorizontal),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(spacing.xxl))

        Box(
            modifier = Modifier
                .size(80.dp)
                .background(
                    Brush.linearGradient(
                        listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.tertiary
                        )
                    ),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                state.userName.take(1).uppercase(),
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(spacing.lg))
        Text(
            state.userName,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(spacing.xs))
        Text(
            state.userEmail,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(spacing.md))

        Text(
            text = state.userRole,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
                .padding(horizontal = spacing.md, vertical = spacing.xs)
        )

        Spacer(modifier = Modifier.height(spacing.xl))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.outlineVariant
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            ProfileRow(Icons.Default.PersonOutline, "Edit profile") { onNavigateToModule("/profile") }
            RowDivider()
            ProfileRow(Icons.Default.Lock, "Change password", onClick = onChangePassword)
            RowDivider()
            ProfileRow(Icons.Default.NotificationsNone, "Notifications", onClick = onNotificationsClick)
            RowDivider()
            ProfileRow(Icons.AutoMirrored.Filled.HelpOutline, "Help & support") { onNavigateToModule("/help") }
            RowDivider()
            ProfileRow(Icons.Default.Info, "About OneApp") { onNavigateToModule("/about") }
        }

        Spacer(modifier = Modifier.height(spacing.lg))

        Card(
            onClick = onSignOut,
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 52.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.Logout,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.onErrorContainer
                )
                Spacer(modifier = Modifier.width(spacing.sm))
                Text(
                    "Sign out",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }

        Spacer(modifier = Modifier.height(spacing.xxl))
    }
}

@Composable
private fun RowDivider() {
    HorizontalDivider(
        color = MaterialTheme.colorScheme.outlineVariant,
        modifier = Modifier.padding(start = 56.dp)
    )
}

@Composable
private fun ProfileRow(icon: ImageVector, title: String, onClick: () -> Unit = {}) {
    val spacing = LocalSpacing.current

    Surface(
        onClick = onClick,
        color = Color.Transparent,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .heightIn(min = 56.dp)
                .padding(horizontal = spacing.lg),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(spacing.lg))
            Text(
                title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
        }
    }
}
