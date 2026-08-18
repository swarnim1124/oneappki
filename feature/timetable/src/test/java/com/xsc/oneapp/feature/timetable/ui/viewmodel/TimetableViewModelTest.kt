package com.xsc.oneapp.feature.timetable.ui.viewmodel

import app.cash.turbine.test
import com.xsc.oneapp.core.result.UiState
import com.xsc.oneapp.feature.timetable.domain.model.AcademicCalendar
import com.xsc.oneapp.feature.timetable.domain.model.FacultyAllocation
import com.xsc.oneapp.feature.timetable.domain.model.RoomAllocation
import com.xsc.oneapp.feature.timetable.domain.model.Substitution
import com.xsc.oneapp.feature.timetable.domain.model.TimeSlot
import com.xsc.oneapp.feature.timetable.domain.model.TimetableApproval
import com.xsc.oneapp.feature.timetable.domain.model.TimetableEntry
import com.xsc.oneapp.feature.timetable.domain.model.TimetableExportResult
import com.xsc.oneapp.feature.timetable.domain.model.TimetableFilter
import com.xsc.oneapp.feature.timetable.domain.model.TimetablePerspective
import com.xsc.oneapp.feature.timetable.domain.model.WorkingDay
import com.xsc.oneapp.feature.timetable.domain.usecase.CancelSubstitutionUseCase
import com.xsc.oneapp.feature.timetable.domain.usecase.DecideTimetableApprovalUseCase
import com.xsc.oneapp.feature.timetable.domain.usecase.ExportTimetableUseCase
import com.xsc.oneapp.feature.timetable.domain.usecase.GetAcademicCalendarUseCase
import com.xsc.oneapp.feature.timetable.domain.usecase.GetFacultyAllocationsUseCase
import com.xsc.oneapp.feature.timetable.domain.usecase.GetRoomAllocationsUseCase
import com.xsc.oneapp.feature.timetable.domain.usecase.GetSubstitutionsUseCase
import com.xsc.oneapp.feature.timetable.domain.usecase.GetTimeSlotsUseCase
import com.xsc.oneapp.feature.timetable.domain.usecase.GetTimetableApprovalsUseCase
import com.xsc.oneapp.feature.timetable.domain.usecase.GetTimetableEntriesUseCase
import com.xsc.oneapp.feature.timetable.domain.usecase.GetWorkingDaysUseCase
import com.xsc.oneapp.feature.timetable.domain.usecase.RequestSubstitutionUseCase
import com.xsc.oneapp.feature.timetable.domain.usecase.SubmitTimetableApprovalUseCase
import com.xsc.sdk.auth.SessionManager
import com.xsc.sdk.network.APIError
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Covers the unified-screen [TimetableViewModel] (spec §17): all eight `view`
 * sections load together in [TimetableViewModel.init] rather than behind a
 * per-tab gate (see that class's kdoc for why), and the mutation surface is
 * filter/perspective/search/selectEntry plus the five write actions the
 * details drawer and header expose - not the old tab-index API.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class TimetableViewModelTest {

    private lateinit var getTimetableEntriesUseCase: GetTimetableEntriesUseCase
    private lateinit var getWorkingDaysUseCase: GetWorkingDaysUseCase
    private lateinit var getTimeSlotsUseCase: GetTimeSlotsUseCase
    private lateinit var getAcademicCalendarUseCase: GetAcademicCalendarUseCase
    private lateinit var getFacultyAllocationsUseCase: GetFacultyAllocationsUseCase
    private lateinit var getRoomAllocationsUseCase: GetRoomAllocationsUseCase
    private lateinit var getSubstitutionsUseCase: GetSubstitutionsUseCase
    private lateinit var getTimetableApprovalsUseCase: GetTimetableApprovalsUseCase
    private lateinit var exportTimetableUseCase: ExportTimetableUseCase
    private lateinit var submitTimetableApprovalUseCase: SubmitTimetableApprovalUseCase
    private lateinit var decideTimetableApprovalUseCase: DecideTimetableApprovalUseCase
    private lateinit var requestSubstitutionUseCase: RequestSubstitutionUseCase
    private lateinit var cancelSubstitutionUseCase: CancelSubstitutionUseCase
    private lateinit var sessionManager: SessionManager

    private val sampleEntry = TimetableEntry(
        id = "1",
        ttId = "5",
        institutionId = "1",
        academicYearId = "2026",
        termId = "1",
        programId = "10",
        semesterId = "2",
        sectionId = "3",
        courseId = "CS101",
        courseOfferingId = "1",
        facultyId = "201",
        facultyCourseAssignmentId = "201",
        workingDayId = "1",
        dayOfWeek = "MONDAY",
        timeSlotId = "2",
        roomId = "50",
        sessionTypeId = "LECTURE",
        startDate = "2026-01-01",
        endDate = "2026-06-30",
        isActive = "true",
        ttCode = "TT_SEC3_TERM1",
        ttStatus = "PUBLISHED"
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        getTimetableEntriesUseCase = mockk()
        getWorkingDaysUseCase = mockk()
        getTimeSlotsUseCase = mockk()
        getAcademicCalendarUseCase = mockk()
        getFacultyAllocationsUseCase = mockk()
        getRoomAllocationsUseCase = mockk()
        getSubstitutionsUseCase = mockk()
        getTimetableApprovalsUseCase = mockk()
        exportTimetableUseCase = mockk()
        submitTimetableApprovalUseCase = mockk()
        decideTimetableApprovalUseCase = mockk()
        requestSubstitutionUseCase = mockk()
        cancelSubstitutionUseCase = mockk()
        sessionManager = mockk(relaxed = true)

        // Every test constructs the ViewModel, which loads all eight sections from
        // init{} - give every use case a harmless default so a test that only cares
        // about e.g. export doesn't have to stub the other seven every time.
        coEvery { getTimetableEntriesUseCase(any()) } returns emptyList()
        coEvery { getWorkingDaysUseCase() } returns emptyList()
        coEvery { getTimeSlotsUseCase() } returns emptyList()
        coEvery { getAcademicCalendarUseCase() } returns null
        coEvery { getFacultyAllocationsUseCase() } returns emptyList()
        coEvery { getRoomAllocationsUseCase() } returns emptyList()
        coEvery { getSubstitutionsUseCase() } returns emptyList()
        coEvery { getTimetableApprovalsUseCase() } returns emptyList()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun viewModel(): TimetableViewModel = TimetableViewModel(
        getTimetableEntriesUseCase,
        getWorkingDaysUseCase,
        getTimeSlotsUseCase,
        getAcademicCalendarUseCase,
        getFacultyAllocationsUseCase,
        getRoomAllocationsUseCase,
        getSubstitutionsUseCase,
        getTimetableApprovalsUseCase,
        exportTimetableUseCase,
        submitTimetableApprovalUseCase,
        decideTimetableApprovalUseCase,
        requestSubstitutionUseCase,
        cancelSubstitutionUseCase,
        sessionManager
    )

    @Test
    fun `all eight sub-modules surface a Success state on init, with no tab gate`() = runTest {
        val workingDay = WorkingDay("1", "1", "2026", "Standard Week", "2026-01-01", "2026-12-31", "1", "MONDAY", "true", "true")
        val timeSlot = TimeSlot("1", "1", "Period 1", "09:00:00", "09:50:00", "1", "false", "true")
        val calendar = AcademicCalendar("1", "2026", "1", "proxied term data")
        val facultyAllocation = FacultyAllocation("1", "12", "201", "PRIMARY", "100", "true", "Assigned HOD approved")
        val roomAllocation = RoomAllocation("1", "5", "50", "MONDAY", "2")
        val substitution = Substitution("1", "501", "1", "FAC_CHANGE", "201", "205", "50", "50", "2026-02-15", "2026-02-15", "Faculty on sick leave", "ACTIVE")
        val approval = TimetableApproval("5", "1", "2026", "1", "3", "TT_SEC3_TERM1", "PENDING_APPROVAL", "Timetable draft submitted for Dean review")

        coEvery { getTimetableEntriesUseCase(any()) } returns listOf(sampleEntry)
        coEvery { getWorkingDaysUseCase() } returns listOf(workingDay)
        coEvery { getTimeSlotsUseCase() } returns listOf(timeSlot)
        coEvery { getAcademicCalendarUseCase() } returns calendar
        coEvery { getFacultyAllocationsUseCase() } returns listOf(facultyAllocation)
        coEvery { getRoomAllocationsUseCase() } returns listOf(roomAllocation)
        coEvery { getSubstitutionsUseCase() } returns listOf(substitution)
        coEvery { getTimetableApprovalsUseCase() } returns listOf(approval)

        val vm = viewModel()

        assertEquals(listOf(sampleEntry), (vm.entriesState.value as UiState.Success).data)
        assertEquals(listOf(workingDay), (vm.workingDaysState.value as UiState.Success).data)
        assertEquals(listOf(timeSlot), (vm.timeSlotsState.value as UiState.Success).data)
        assertEquals(calendar, (vm.academicCalendarState.value as UiState.Success).data)
        assertEquals(listOf(facultyAllocation), (vm.facultyAllocationsState.value as UiState.Success).data)
        assertEquals(listOf(roomAllocation), (vm.roomAllocationsState.value as UiState.Success).data)
        assertEquals(listOf(substitution), (vm.substitutionsState.value as UiState.Success).data)
        assertEquals(listOf(approval), (vm.approvalsState.value as UiState.Success).data)
        assertEquals(listOf(sampleEntry), vm.knownEntries.value)
    }

    @Test
    fun `a null academic calendar is still a Success state, not an error`() = runTest {
        val vm = viewModel()

        val state = vm.academicCalendarState.value as UiState.Success
        assertNull(state.data)
    }

    @Test
    fun `a failing sub-module surfaces its error without blocking the others`() = runTest {
        coEvery { getSubstitutionsUseCase() } throws APIError.HttpError(500, "boom")

        val vm = viewModel()

        val substitutionsState = vm.substitutionsState.value as UiState.UnexpectedError
        assertTrue(substitutionsState.message.contains("500"))
        assertTrue((vm.entriesState.value as UiState.Success).data.isEmpty())
    }

    @Test
    fun `retryEntries retries only the schedule section`() = runTest {
        coEvery { getTimetableEntriesUseCase(any()) } throws APIError.NetworkError("offline") andThen listOf(sampleEntry)

        val vm = viewModel()
        assertTrue(vm.entriesState.value is UiState.NetworkError)
        assertTrue(vm.workingDaysState.value is UiState.Success)

        vm.retryEntries()

        assertEquals(listOf(sampleEntry), (vm.entriesState.value as UiState.Success).data)
    }

    @Test
    fun `refreshAll reloads every section`() = runTest {
        val vm = viewModel()
        coEvery { getTimetableEntriesUseCase(any()) } returns listOf(sampleEntry)
        coEvery { getWorkingDaysUseCase() } throws APIError.HttpError(500, "boom")

        vm.refreshAll()

        assertEquals(listOf(sampleEntry), (vm.entriesState.value as UiState.Success).data)
        assertTrue(vm.workingDaysState.value is UiState.UnexpectedError)
    }

    @Test
    fun `updateFilter narrows one field without clobbering an already-chosen one`() = runTest {
        val vm = viewModel()

        vm.updateFilter { it.copy(termId = "1") }
        vm.updateFilter { it.copy(facultyId = "201") }

        assertEquals(TimetableFilter(termId = "1", facultyId = "201"), vm.filter.value)
        coVerify(atLeast = 1) { getTimetableEntriesUseCase(TimetableFilter(termId = "1", facultyId = "201")) }
    }

    @Test
    fun `clearFilters resets to no filter and reloads`() = runTest {
        val vm = viewModel()
        vm.updateFilter { it.copy(sectionId = "3") }
        assertTrue(vm.filter.value.isActive)

        vm.clearFilters()

        assertEquals(TimetableFilter(), vm.filter.value)
        assertFalse(vm.filter.value.isActive)
    }

    @Test
    fun `setPerspective re-emphasizes the same data rather than refetching`() = runTest {
        val vm = viewModel()

        vm.setPerspective(TimetablePerspective.FACULTY)

        assertEquals(TimetablePerspective.FACULTY, vm.perspective.value)
        coVerify(exactly = 1) { getTimetableEntriesUseCase(any()) }
    }

    @Test
    fun `setSearchQuery and selectEntry update their own state`() = runTest {
        val vm = viewModel()

        vm.setSearchQuery("CS101")
        vm.selectEntry(sampleEntry)

        assertEquals("CS101", vm.searchQuery.value)
        assertEquals(sampleEntry, vm.selectedEntry.value)

        vm.selectEntry(null)
        assertNull(vm.selectedEntry.value)
    }

    @Test
    fun `permissions mirrors the session manager's current permissions`() = runTest {
        every { sessionManager.currentPermissions } returns kotlinx.coroutines.flow.MutableStateFlow(
            listOf("timetable.timetable.view", "timetable.timetableApproval.update")
        )

        val vm = viewModel()

        assertEquals(listOf("timetable.timetable.view", "timetable.timetableApproval.update"), vm.permissions.value)
    }

    @Test
    fun `export notifies with the filename and record count on success`() = runTest {
        coEvery { exportTimetableUseCase("pdf", any()) } returns
            TimetableExportResult("pdf", "application/pdf", "timetable_sec3.pdf", 24)

        val vm = viewModel()

        vm.effects.test {
            vm.export("pdf")
            val effect = awaitItem() as TimetableEffect.Notify
            assertTrue(effect.message.contains("timetable_sec3.pdf"))
            assertTrue(effect.message.contains("24"))
            assertFalse(effect.isError)
            cancelAndIgnoreRemainingEvents()
        }
        assertFalse(vm.exportInFlight.value)
    }

    @Test
    fun `export surfaces the backend's error message`() = runTest {
        coEvery { exportTimetableUseCase(any(), any()) } throws APIError.HttpError(500, "export failed")

        val vm = viewModel()

        vm.effects.test {
            vm.export("csv")
            val effect = awaitItem() as TimetableEffect.Notify
            assertTrue(effect.isError)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `submitForApproval reloads approvals and entries and notifies`() = runTest {
        coEvery { submitTimetableApprovalUseCase("5", "ready") } returns
            TimetableApproval("5", "1", "2026", "1", "3", "TT_SEC3_TERM1", "PENDING_APPROVAL", null)
        coEvery { getTimetableApprovalsUseCase() } returns emptyList() andThen listOf(
            TimetableApproval("5", "1", "2026", "1", "3", "TT_SEC3_TERM1", "PENDING_APPROVAL", null)
        )

        val vm = viewModel()

        vm.effects.test {
            vm.submitForApproval("5", "ready")
            val effect = awaitItem() as TimetableEffect.Notify
            assertTrue(effect.message.contains("Submitted"))
            cancelAndIgnoreRemainingEvents()
        }
        assertEquals("PENDING_APPROVAL", (vm.approvalsState.value as UiState.Success).data.first().statusId)
        coVerify(atLeast = 2) { getTimetableEntriesUseCase(any()) } // init load + post-submit reload
    }

    @Test
    fun `decideApproval reports approve vs reject distinctly`() = runTest {
        coEvery { decideTimetableApprovalUseCase("5", true, null) } returns
            TimetableApproval("5", "1", "2026", "1", "3", "TT_SEC3_TERM1", "PUBLISHED", null)

        val vm = viewModel()

        vm.effects.test {
            vm.decideApproval("5", approve = true, remarks = null)
            val effect = awaitItem() as TimetableEffect.Notify
            assertTrue(effect.message.contains("published"))
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `decideApproval reject returns the timetable to draft`() = runTest {
        coEvery { decideTimetableApprovalUseCase("5", false, "needs fixes") } returns
            TimetableApproval("5", "1", "2026", "1", "3", "TT_SEC3_TERM1", "DRAFT", "needs fixes")

        val vm = viewModel()

        vm.effects.test {
            vm.decideApproval("5", approve = false, remarks = "needs fixes")
            val effect = awaitItem() as TimetableEffect.Notify
            assertTrue(effect.message.contains("rejected"))
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `requestSubstitution reloads substitutions and notifies`() = runTest {
        coEvery {
            requestSubstitutionUseCase("1", "205", "sick leave", "201", "2026-02-15", null)
        } returns Substitution("2", "501", "1", "FAC_CHANGE", "201", "205", "50", "50", "2026-02-15", "2026-02-15", "sick leave", "ACTIVE")
        coEvery { getSubstitutionsUseCase() } returns emptyList() andThen listOf(
            Substitution("2", "501", "1", "FAC_CHANGE", "201", "205", "50", "50", "2026-02-15", "2026-02-15", "sick leave", "ACTIVE")
        )

        val vm = viewModel()

        vm.effects.test {
            vm.requestSubstitution(
                timetableEntryId = "1",
                substituteFacultyId = "205",
                reason = "sick leave",
                originalFacultyId = "201",
                substitutionDate = "2026-02-15",
                remarks = null
            )
            val effect = awaitItem() as TimetableEffect.Notify
            assertTrue(effect.message.contains("recorded"))
            cancelAndIgnoreRemainingEvents()
        }
        assertEquals(1, (vm.substitutionsState.value as UiState.Success).data.size)
    }

    @Test
    fun `cancelSubstitution notifies and reloads substitutions`() = runTest {
        coEvery { cancelSubstitutionUseCase("2", "no longer needed") } returns Unit

        val vm = viewModel()

        vm.effects.test {
            vm.cancelSubstitution("2", "no longer needed")
            val effect = awaitItem() as TimetableEffect.Notify
            assertTrue(effect.message.contains("cancelled"))
            cancelAndIgnoreRemainingEvents()
        }
        assertFalse(vm.substitutionInFlight.value)
    }

    @Test
    fun `a mutation error is reported without leaving the in-flight flag stuck`() = runTest {
        coEvery { cancelSubstitutionUseCase(any(), any()) } throws APIError.HttpError(409, "already cancelled")

        val vm = viewModel()

        vm.effects.test {
            vm.cancelSubstitution("2", "dup")
            val effect = awaitItem() as TimetableEffect.Notify
            assertTrue(effect.isError)
            cancelAndIgnoreRemainingEvents()
        }
        assertFalse(vm.substitutionInFlight.value)
    }
}
