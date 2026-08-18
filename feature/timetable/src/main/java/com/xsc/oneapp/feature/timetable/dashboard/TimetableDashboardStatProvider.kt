package com.xsc.oneapp.feature.timetable.dashboard

import com.xsc.oneapp.core.dashboard.DashboardStatContribution
import com.xsc.oneapp.core.dashboard.DashboardStatProvider
import com.xsc.oneapp.feature.timetable.domain.model.TimeSlot
import com.xsc.oneapp.feature.timetable.domain.model.TimetableEntry
import com.xsc.oneapp.feature.timetable.domain.model.TimetablePermissions
import com.xsc.oneapp.feature.timetable.domain.model.WorkingDay
import com.xsc.oneapp.feature.timetable.domain.usecase.GetTimeSlotsUseCase
import com.xsc.oneapp.feature.timetable.domain.usecase.GetTimetableEntriesUseCase
import com.xsc.oneapp.feature.timetable.domain.usecase.GetWorkingDaysUseCase
import com.xsc.oneapp.feature.timetable.ui.components.courseLabel
import com.xsc.oneapp.feature.timetable.ui.components.displayRange
import com.xsc.oneapp.feature.timetable.ui.components.roomLabel
import com.xsc.sdk.auth.SessionManager
import kotlinx.coroutines.CancellationException
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

/**
 * Feeds the Dashboard's "nextclass" stat card from the same `timetable`/`workingDay`/
 * `timeSlot` view calls the Timetable module's own Schedule tab already uses (see
 * [WeeklySchedule] for the day-matching join this mirrors) - no new endpoint.
 *
 * Previously this card was a static "Data Structures / 10:00 AM • Room 302" with no
 * data source at all (see DashboardViewModel.getMockStats). m_timetable resolves
 * none of its course/room foreign keys to names (TimetableNotes.kt), so there is no
 * real "Data Structures" to show - the honest replacement uses the same id-based
 * [courseLabel]/[roomLabel] fallbacks the Schedule tab itself renders, with a real
 * time slot instead of a fabricated one.
 *
 * Registered via [com.xsc.oneapp.feature.timetable.di.TimetableModule]'s
 * `@Binds @IntoSet`, joining the extension point [DashboardStatProvider] documents.
 *
 * Returns null (leaving the Dashboard's own "Coming Soon" placeholder) when the user
 * lacks `timetable.timetable.view`, there is no session, the calls fail, or there is
 * no more class left to attend today - never a fabricated class.
 */
class TimetableDashboardStatProvider @Inject constructor(
    private val getTimetableEntriesUseCase: GetTimetableEntriesUseCase,
    private val getTimeSlotsUseCase: GetTimeSlotsUseCase,
    private val getWorkingDaysUseCase: GetWorkingDaysUseCase,
    private val sessionManager: SessionManager
) : DashboardStatProvider {

    override val statId: String = STAT_ID

    override suspend fun provideStat(): DashboardStatContribution? {
        if (!sessionManager.hasPermission(TimetablePermissions.TIMETABLE_VIEW)) return null

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
            // No session, request failed, module unconfigured on this tenant, etc. -
            // fall back to the Dashboard's own placeholder rather than showing wrong data.
            return null
        }

        val next = nextClassToday(entries, timeSlots, workingDays, LocalDate.now(), LocalTime.now())
            ?: return null

        return DashboardStatContribution(
            id = STAT_ID,
            title = "Next Class",
            value = next.entry.courseLabel(),
            tag = listOfNotNull(next.slot.displayRange(), next.entry.roomLabel()).joinToString(" • ")
        )
    }

    private data class UpcomingEntry(val entry: TimetableEntry, val slot: TimeSlot, val start: LocalTime)

    /**
     * Pure function (no DI, no clock read) so it's directly unit-testable: mirrors
     * [com.xsc.oneapp.feature.timetable.domain.model.WeeklySchedule]'s two-path day
     * match (working-day id first, day-of-week name as fallback - see that class's
     * kdoc for why both are needed), then picks the earliest time slot that hasn't
     * started yet.
     */
    internal fun nextClassToday(
        entries: List<TimetableEntry>,
        timeSlots: List<TimeSlot>,
        workingDays: List<WorkingDay>,
        today: LocalDate,
        now: LocalTime
    ): UpcomingEntry? {
        val todayName = today.dayOfWeek.name // e.g. "MONDAY" - matches WeeklySchedule.WEEK_ORDER
        val todayWorkingDayIds = workingDays
            .filter { (it.dayName ?: it.name)?.equals(todayName, ignoreCase = true) == true }
            .mapNotNull { it.id ?: it.dayOfWeekId }
            .toSet()

        val todaysEntries = entries.filter { entry ->
            (entry.dayOfWeek != null && entry.dayOfWeek.equals(todayName, ignoreCase = true)) ||
                (entry.workingDayId != null && entry.workingDayId in todayWorkingDayIds)
        }
        if (todaysEntries.isEmpty()) return null

        val slotById = timeSlots.associateBy { it.id }
        return todaysEntries
            .mapNotNull { entry ->
                val slot = entry.timeSlotId?.let { slotById[it] } ?: return@mapNotNull null
                val start = slot.startTime?.take(5)?.let { runCatching { LocalTime.parse(it) }.getOrNull() }
                    ?: return@mapNotNull null
                UpcomingEntry(entry, slot, start)
            }
            .filter { it.start >= now }
            .minByOrNull { it.start }
    }

    companion object {
        const val STAT_ID = "nextclass"
    }
}
