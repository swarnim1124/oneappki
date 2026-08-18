package com.xsc.oneapp.feature.timetable.dashboard

import com.xsc.oneapp.core.dashboard.DashboardTimelinePoint
import com.xsc.oneapp.core.dashboard.DashboardTimelineProvider
import com.xsc.oneapp.feature.timetable.domain.model.TimeSlot
import com.xsc.oneapp.feature.timetable.domain.model.TimetableEntry
import com.xsc.oneapp.feature.timetable.domain.model.TimetablePermissions
import com.xsc.oneapp.feature.timetable.domain.model.WorkingDay
import com.xsc.oneapp.feature.timetable.domain.usecase.GetTimeSlotsUseCase
import com.xsc.oneapp.feature.timetable.domain.usecase.GetTimetableEntriesUseCase
import com.xsc.oneapp.feature.timetable.domain.usecase.GetWorkingDaysUseCase
import com.xsc.sdk.auth.SessionManager
import kotlinx.coroutines.CancellationException
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

/**
 * Feeds the Dashboard's "Today's Timeline" card from the day's real time slots.
 *
 * Previously this card ([com.xsc.oneapp.feature.dashboard.ui.components.TodayTimelineCard])
 * rendered 4 hardcoded points ("09:00 done / 10:00 current / 13:00, 15:00 upcoming")
 * on every load, for every user, regardless of what day it was - see that file's git
 * history. This computes the same DONE/CURRENT/UPCOMING shape from today's actual
 * time slots (m_timetable's `timeSlot`/`workingDay`/`timetable` view calls, the same
 * three this module's own Schedule tab uses).
 *
 * Registered via [com.xsc.oneapp.feature.timetable.di.TimetableModule]'s
 * `@Binds @IntoSet`. Returns an empty list (Dashboard hides the card) without
 * `timetable.timetable.view` permission, with no session, on a failed call, or when
 * there is simply no schedule for today.
 */
class TimetableTimelineProvider @Inject constructor(
    private val getTimetableEntriesUseCase: GetTimetableEntriesUseCase,
    private val getTimeSlotsUseCase: GetTimeSlotsUseCase,
    private val getWorkingDaysUseCase: GetWorkingDaysUseCase,
    private val sessionManager: SessionManager
) : DashboardTimelineProvider {

    override suspend fun provideTimeline(): List<DashboardTimelinePoint> {
        if (!sessionManager.hasPermission(TimetablePermissions.TIMETABLE_VIEW)) return emptyList()

        val entries: List<TimetableEntry>
        val timeSlots: List<TimeSlot>
        val workingDays: List<WorkingDay>
        try {
            entries = getTimetableEntriesUseCase()
            timeSlots = getTimeSlotsUseCase()
            workingDays = getWorkingDaysUseCase()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            return emptyList()
        }

        return todaysTimeline(entries, timeSlots, workingDays, LocalDate.now(), LocalTime.now())
    }

    /** Pure and unit-testable, mirroring [TimetableDashboardStatProvider.nextClassToday]'s
     * day-matching join. A slot is CURRENT while `now` falls inside its [start, end)
     * range (falling back to a 1-hour window when the backend hasn't sent an
     * `endTime` for that slot), DONE if it has already ended, else UPCOMING. */
    internal fun todaysTimeline(
        entries: List<TimetableEntry>,
        timeSlots: List<TimeSlot>,
        workingDays: List<WorkingDay>,
        today: LocalDate,
        now: LocalTime
    ): List<DashboardTimelinePoint> {
        val todayName = today.dayOfWeek.name
        val todayWorkingDayIds = workingDays
            .filter { (it.dayName ?: it.name)?.equals(todayName, ignoreCase = true) == true }
            .mapNotNull { it.id ?: it.dayOfWeekId }
            .toSet()

        val todaysSlotIds = entries
            .filter { entry ->
                (entry.dayOfWeek != null && entry.dayOfWeek.equals(todayName, ignoreCase = true)) ||
                    (entry.workingDayId != null && entry.workingDayId in todayWorkingDayIds)
            }
            .mapNotNull { it.timeSlotId }
            .toSet()

        return timeSlots
            .filter { it.id in todaysSlotIds && it.startTime != null }
            .mapNotNull { slot ->
                val start = slot.startTime?.take(5)?.let { runCatching { LocalTime.parse(it) }.getOrNull() }
                    ?: return@mapNotNull null
                val end = slot.endTime?.take(5)?.let { runCatching { LocalTime.parse(it) }.getOrNull() }
                    ?: start.plus(Duration.ofHours(1))
                val state = when {
                    now.isBefore(start) -> DashboardTimelinePoint.State.UPCOMING
                    now.isBefore(end) -> DashboardTimelinePoint.State.CURRENT
                    else -> DashboardTimelinePoint.State.DONE
                }
                DashboardTimelinePoint(label = slot.startTime.take(5), state = state)
            }
            .sortedBy { it.label }
    }
}
