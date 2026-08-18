package com.xsc.oneapp.feature.timetable.dashboard

import com.xsc.oneapp.core.dashboard.DashboardTimelinePoint
import com.xsc.oneapp.feature.timetable.domain.model.TimeSlot
import com.xsc.oneapp.feature.timetable.domain.model.TimetableEntry
import com.xsc.oneapp.feature.timetable.domain.usecase.GetTimeSlotsUseCase
import com.xsc.oneapp.feature.timetable.domain.usecase.GetTimetableEntriesUseCase
import com.xsc.oneapp.feature.timetable.domain.usecase.GetWorkingDaysUseCase
import com.xsc.sdk.auth.SessionManager
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime

class TimetableTimelineProviderTest {

    private fun provider() = TimetableTimelineProvider(
        mockk<GetTimetableEntriesUseCase>(),
        mockk<GetTimeSlotsUseCase>(),
        mockk<GetWorkingDaysUseCase>(),
        mockk<SessionManager>()
    )

    private fun entry(timeSlotId: String) = TimetableEntry(
        id = "e", ttId = null, institutionId = null, academicYearId = null, termId = null,
        programId = null, semesterId = null, sectionId = null, courseId = "C1",
        courseOfferingId = null, facultyId = null, facultyCourseAssignmentId = null,
        workingDayId = null, dayOfWeek = "MONDAY", timeSlotId = timeSlotId,
        roomId = "R1", sessionTypeId = null, startDate = null, endDate = null,
        isActive = "true", ttCode = null, ttStatus = null
    )

    private fun slot(id: String, start: String, end: String) =
        TimeSlot(id, null, null, start, end, null, "false", "true")

    @Test
    fun `classifies each of today's slots as done, current or upcoming relative to now`() {
        val entries = listOf(entry("s1"), entry("s2"), entry("s3"))
        val slots = listOf(
            slot("s1", "08:00:00", "09:00:00"),
            slot("s2", "10:00:00", "11:00:00"),
            slot("s3", "13:00:00", "14:00:00")
        )

        val timeline = provider().todaysTimeline(
            entries, slots, emptyList(),
            today = LocalDate.of(2026, 8, 17), // Monday
            now = LocalTime.of(10, 30)
        )

        assertEquals(
            listOf(
                DashboardTimelinePoint("08:00", DashboardTimelinePoint.State.DONE),
                DashboardTimelinePoint("10:00", DashboardTimelinePoint.State.CURRENT),
                DashboardTimelinePoint("13:00", DashboardTimelinePoint.State.UPCOMING)
            ),
            timeline
        )
    }

    @Test
    fun `no slots today returns an empty timeline, not fabricated points`() {
        val timeline = provider().todaysTimeline(
            emptyList(), emptyList(), emptyList(),
            today = LocalDate.of(2026, 8, 17),
            now = LocalTime.of(10, 0)
        )

        assertEquals(emptyList<DashboardTimelinePoint>(), timeline)
    }
}
