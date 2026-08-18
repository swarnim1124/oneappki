package com.xsc.oneapp.feature.timetable.dashboard

import com.xsc.oneapp.feature.timetable.domain.model.TimeSlot
import com.xsc.oneapp.feature.timetable.domain.model.TimetableEntry
import com.xsc.oneapp.feature.timetable.domain.model.TimetablePermissions
import com.xsc.oneapp.feature.timetable.domain.model.WorkingDay
import com.xsc.oneapp.feature.timetable.domain.usecase.GetTimeSlotsUseCase
import com.xsc.oneapp.feature.timetable.domain.usecase.GetTimetableEntriesUseCase
import com.xsc.oneapp.feature.timetable.domain.usecase.GetWorkingDaysUseCase
import com.xsc.sdk.auth.SessionManager
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

class TimetableDashboardStatProviderTest {

    private fun entry(dayOfWeek: String? = "MONDAY", workingDayId: String? = null, timeSlotId: String?, courseId: String?, roomId: String?) =
        TimetableEntry(
            id = "e1", ttId = null, institutionId = null, academicYearId = null, termId = null,
            programId = null, semesterId = null, sectionId = null, courseId = courseId,
            courseOfferingId = null, facultyId = null, facultyCourseAssignmentId = null,
            workingDayId = workingDayId, dayOfWeek = dayOfWeek, timeSlotId = timeSlotId,
            roomId = roomId, sessionTypeId = null, startDate = null, endDate = null,
            isActive = "true", ttCode = null, ttStatus = null
        )

    private fun slot(id: String, startTime: String) =
        TimeSlot(id, null, null, startTime, null, null, "false", "true")

    @Test
    fun `statId matches the Dashboard's next-class placeholder`() {
        val provider = TimetableDashboardStatProvider(mockk(), mockk(), mockk(), mockk())
        assertEquals("nextclass", provider.statId)
    }

    @Test
    fun `picks the earliest slot that has not started yet, by day-of-week match`() {
        val provider = TimetableDashboardStatProvider(mockk(), mockk(), mockk(), mockk())
        val entries = listOf(
            entry(dayOfWeek = "MONDAY", timeSlotId = "slot-early", courseId = "C1", roomId = "R1"),
            entry(dayOfWeek = "MONDAY", timeSlotId = "slot-next", courseId = "C2", roomId = "R2"),
            entry(dayOfWeek = "MONDAY", timeSlotId = "slot-late", courseId = "C3", roomId = "R3")
        )
        val slots = listOf(slot("slot-early", "08:00:00"), slot("slot-next", "11:00:00"), slot("slot-late", "15:00:00"))

        val next = provider.nextClassToday(
            entries, slots, emptyList(),
            today = LocalDate.of(2026, 8, 17), // a Monday
            now = LocalTime.of(10, 0)
        )

        assertEquals("C2", next?.entry?.courseId)
        assertEquals("slot-next", next?.slot?.id)
    }

    @Test
    fun `matches via working-day id when dayOfWeek is not on the entry`() {
        val provider = TimetableDashboardStatProvider(mockk(), mockk(), mockk(), mockk())
        val entries = listOf(entry(dayOfWeek = null, workingDayId = "wd-mon", timeSlotId = "s1", courseId = "C1", roomId = "R1"))
        val slots = listOf(slot("s1", "09:00:00"))
        val workingDays = listOf(WorkingDay(id = "wd-mon", institutionId = null, academicYearId = null, name = "Monday", effectiveFrom = null, effectiveTo = null, dayOfWeekId = null, dayName = "Monday", isWorkingDay = "true", isActive = "true"))

        val next = provider.nextClassToday(entries, slots, workingDays, today = LocalDate.of(2026, 8, 17), now = LocalTime.of(8, 0))

        assertEquals("C1", next?.entry?.courseId)
    }

    @Test
    fun `returns null when every class today has already started`() {
        val provider = TimetableDashboardStatProvider(mockk(), mockk(), mockk(), mockk())
        val entries = listOf(entry(timeSlotId = "s1", courseId = "C1", roomId = "R1"))
        val slots = listOf(slot("s1", "08:00:00"))

        val next = provider.nextClassToday(entries, slots, emptyList(), today = LocalDate.of(2026, 8, 17), now = LocalTime.of(18, 0))

        assertNull(next)
    }

    @Test
    fun `provideStat returns null without timetable view permission, never showing unauthorized data`() = runTest {
        val sessionManager = mockk<SessionManager>()
        every { sessionManager.hasPermission(TimetablePermissions.TIMETABLE_VIEW) } returns false
        val provider = TimetableDashboardStatProvider(mockk(), mockk(), mockk(), sessionManager)

        val contribution = provider.provideStat()

        assertNull(contribution)
    }

    @Test
    fun `provideStat surfaces a real course-id and room-id label, never a fabricated course name`() = runTest {
        val sessionManager = mockk<SessionManager>()
        every { sessionManager.hasPermission(TimetablePermissions.TIMETABLE_VIEW) } returns true
        val getEntries = mockk<GetTimetableEntriesUseCase>()
        val getSlots = mockk<GetTimeSlotsUseCase>()
        val getWorkingDays = mockk<GetWorkingDaysUseCase>()
        val today = LocalDate.now().dayOfWeek.name
        coEvery { getEntries() } returns listOf(entry(dayOfWeek = today, timeSlotId = "s1", courseId = "4521", roomId = "302"))
        coEvery { getSlots() } returns listOf(slot("s1", LocalTime.now().plusHours(1).toString() + ":00"))
        coEvery { getWorkingDays() } returns emptyList()

        val provider = TimetableDashboardStatProvider(getEntries, getSlots, getWorkingDays, sessionManager)
        val contribution = provider.provideStat()

        assertEquals("nextclass", contribution?.id)
        assertEquals("Course #4521", contribution?.value)
        assertEquals(true, contribution?.tag?.contains("Room #302"))
    }

    @Test
    fun `provideStat falls back to the Dashboard placeholder when the calls fail`() = runTest {
        val sessionManager = mockk<SessionManager>()
        every { sessionManager.hasPermission(TimetablePermissions.TIMETABLE_VIEW) } returns true
        val getEntries = mockk<GetTimetableEntriesUseCase>()
        coEvery { getEntries() } throws RuntimeException("offline")

        val provider = TimetableDashboardStatProvider(getEntries, mockk(), mockk(), sessionManager)

        assertNull(provider.provideStat())
    }
}
