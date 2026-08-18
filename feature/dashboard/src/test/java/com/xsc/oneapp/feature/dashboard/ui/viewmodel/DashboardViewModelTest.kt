package com.xsc.oneapp.feature.dashboard.ui.viewmodel

import com.xsc.oneapp.core.dashboard.DashboardStatContribution
import com.xsc.oneapp.core.dashboard.DashboardStatProvider
import com.xsc.oneapp.core.dashboard.DashboardTimelinePoint
import com.xsc.oneapp.core.dashboard.DashboardTimelineProvider
import com.xsc.oneapp.feature.dashboard.domain.model.ModuleItem
import com.xsc.oneapp.feature.dashboard.domain.model.NotificationGroup
import com.xsc.oneapp.feature.dashboard.domain.model.NotificationItem
import com.xsc.oneapp.feature.dashboard.domain.usecase.GetAccessibleModulesUseCase
import com.xsc.oneapp.feature.dashboard.domain.usecase.GetNotificationsUseCase
import com.xsc.oneapp.feature.dashboard.domain.usecase.GetPinnedModuleIdsUseCase
import com.xsc.oneapp.feature.dashboard.domain.usecase.TogglePinnedModuleUseCase
import com.xsc.sdk.auth.SessionManager
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModelTest {

    private lateinit var sessionManager: SessionManager
    private lateinit var getAccessibleModulesUseCase: GetAccessibleModulesUseCase
    private lateinit var getPinnedModuleIdsUseCase: GetPinnedModuleIdsUseCase
    private lateinit var togglePinnedModuleUseCase: TogglePinnedModuleUseCase
    private lateinit var getNotificationsUseCase: GetNotificationsUseCase

    private val curriculum = ModuleItem(
        "academics", "Curriculum", "school", "/academics", ModuleItem.ModuleStatus.ACTIVE, "#4F46E5"
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        sessionManager = mockk()
        getAccessibleModulesUseCase = mockk()
        getPinnedModuleIdsUseCase = mockk()
        togglePinnedModuleUseCase = mockk()
        getNotificationsUseCase = mockk()
        every { sessionManager.getDisplayName() } returns "Student One"
        every { sessionManager.getFirstName() } returns "Student"
        every { sessionManager.currentEmail } returns MutableStateFlow("student@oneapp.local")
        every { sessionManager.currentRole } returns MutableStateFlow("student")
        every { getPinnedModuleIdsUseCase() } returns emptySet()
        coEvery { getNotificationsUseCase() } returns emptyList()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun viewModel(
        statProviders: Set<DashboardStatProvider> = emptySet(),
        timelineProviders: Set<DashboardTimelineProvider> = emptySet()
    ) = DashboardViewModel(
        sessionManager,
        getAccessibleModulesUseCase,
        getPinnedModuleIdsUseCase,
        togglePinnedModuleUseCase,
        getNotificationsUseCase,
        statProviders,
        timelineProviders
    )

    @Test
    fun `session data populates the user header fields`() = runTest {
        coEvery { getAccessibleModulesUseCase() } returns emptyList()

        val vm = viewModel()

        assertEquals("Student One", vm.state.value.userName)
        assertEquals("Student", vm.state.value.userFirstName)
        assertEquals("student@oneapp.local", vm.state.value.userEmail)
        assertEquals("student", vm.state.value.userRole)
    }

    @Test
    fun `accessible modules from the use case populate the module list and placeholder stats`() = runTest {
        coEvery { getAccessibleModulesUseCase() } returns listOf(curriculum)

        val vm = viewModel()

        assertEquals(1, vm.state.value.modules.size)
        assertEquals("academics", vm.state.value.modules.first().id)
        assertEquals("Curriculum", vm.state.value.modules.first().displayName)
        assertEquals(4, vm.state.value.stats.size)
        assertEquals(4, vm.state.value.quickActions.size)
    }

    @Test
    fun `role subtitle title-cases the backend-provided role rather than fabricating one`() {
        every { sessionManager.currentRole } returns MutableStateFlow("teacher")
        coEvery { getAccessibleModulesUseCase() } returns emptyList()

        val vm = viewModel()

        assertEquals("Teacher", vm.getRoleSubtitle())
    }

    @Test
    fun `pinned module ids are loaded from the use case on init`() = runTest {
        coEvery { getAccessibleModulesUseCase() } returns emptyList()
        every { getPinnedModuleIdsUseCase() } returns setOf("academics")

        val vm = viewModel()

        assertEquals(setOf("academics"), vm.state.value.pinnedModuleIds)
    }

    @Test
    fun `togglePinnedModule delegates to the use case and updates state`() = runTest {
        coEvery { getAccessibleModulesUseCase() } returns emptyList()
        every { togglePinnedModuleUseCase("attendance") } returns setOf("attendance")

        val vm = viewModel()
        vm.togglePinnedModule("attendance")

        assertEquals(setOf("attendance"), vm.state.value.pinnedModuleIds)
    }

    @Test
    fun `openPinPicker and closePinPicker toggle the dialog flag`() = runTest {
        coEvery { getAccessibleModulesUseCase() } returns emptyList()

        val vm = viewModel()
        vm.openPinPicker()
        assertEquals(true, vm.state.value.isPinPickerOpen)

        vm.closePinPicker()
        assertFalse(vm.state.value.isPinPickerOpen)
    }

    @Test
    fun `a registered stat provider overrides its matching placeholder stat`() = runTest {
        coEvery { getAccessibleModulesUseCase() } returns emptyList()
        val attendanceProvider = object : DashboardStatProvider {
            override val statId = "attendance"
            override suspend fun provideStat() =
                DashboardStatContribution("attendance", "ATTENDANCE", "92%", "Live")
        }

        val vm = viewModel(statProviders = setOf(attendanceProvider))

        val attendanceStat = vm.state.value.stats.first { it.id == "attendance" }
        assertEquals("92%", attendanceStat.value)
        assertEquals("Live", attendanceStat.tag)
        val feesStat = vm.state.value.stats.first { it.id == "fees" }
        assertEquals("--", feesStat.value)
    }

    @Test
    fun `a stat provider returning null leaves the placeholder untouched`() = runTest {
        coEvery { getAccessibleModulesUseCase() } returns emptyList()
        val silentProvider = object : DashboardStatProvider {
            override val statId = "attendance"
            override suspend fun provideStat(): DashboardStatContribution? = null
        }

        val vm = viewModel(statProviders = setOf(silentProvider))

        val attendanceStat = vm.state.value.stats.first { it.id == "attendance" }
        assertEquals("--", attendanceStat.value)
    }

    @Test
    fun `notifications are empty rather than fabricated when the repository has nothing`() = runTest {
        coEvery { getAccessibleModulesUseCase() } returns emptyList()
        coEvery { getNotificationsUseCase() } returns emptyList()

        val vm = viewModel()

        assertEquals(emptyList<Any>(), vm.state.value.notifications)
        assertEquals(emptyList<Any>(), vm.state.value.recentActivity)
        assertEquals(0, vm.state.value.unreadNotifications)
    }

    @Test
    fun `real notifications from the use case populate state and the unread count`() = runTest {
        coEvery { getAccessibleModulesUseCase() } returns emptyList()
        val real = NotificationItem(
            id = "n1", title = "Fee due", message = "Balance outstanding",
            timestamp = "1h ago", icon = "ic_rupee", isUnread = true, group = NotificationGroup.TODAY
        )
        coEvery { getNotificationsUseCase() } returns listOf(real)

        val vm = viewModel()

        assertEquals(listOf(real), vm.state.value.notifications)
        assertEquals(listOf(real), vm.state.value.recentActivity)
        assertEquals(1, vm.state.value.unreadNotifications)
    }

    @Test
    fun `today's timeline is empty rather than fabricated when no provider has one`() = runTest {
        coEvery { getAccessibleModulesUseCase() } returns emptyList()

        val vm = viewModel()

        assertEquals(emptyList<DashboardTimelinePoint>(), vm.state.value.todayTimeline)
    }

    @Test
    fun `today's timeline comes from the first provider with a non-empty schedule`() = runTest {
        coEvery { getAccessibleModulesUseCase() } returns emptyList()
        val silentProvider = object : DashboardTimelineProvider {
            override suspend fun provideTimeline(): List<DashboardTimelinePoint> = emptyList()
        }
        val realPoints = listOf(DashboardTimelinePoint("09:00", DashboardTimelinePoint.State.CURRENT))
        val realProvider = object : DashboardTimelineProvider {
            override suspend fun provideTimeline(): List<DashboardTimelinePoint> = realPoints
        }

        val vm = viewModel(timelineProviders = setOf(silentProvider, realProvider))

        assertEquals(realPoints, vm.state.value.todayTimeline)
    }
}
