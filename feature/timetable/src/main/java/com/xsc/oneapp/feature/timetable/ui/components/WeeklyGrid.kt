package com.xsc.oneapp.feature.timetable.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xsc.oneapp.feature.timetable.domain.model.ScheduleDay
import com.xsc.oneapp.feature.timetable.domain.model.Substitution
import com.xsc.oneapp.feature.timetable.domain.model.TimeSlot
import com.xsc.oneapp.feature.timetable.domain.model.TimetableEntry
import com.xsc.oneapp.feature.timetable.domain.model.WeeklySchedule
import com.xsc.sdk.theme.LocalSpacing
import com.xsc.sdk.theme.LocalStatusColors

private val PERIOD_COLUMN_WIDTH = 92.dp
private val DAY_COLUMN_WIDTH = 148.dp
private val ROW_HEIGHT = 92.dp

/** What a cell needs to render: its entries, plus whether any of them collide on
 * room or faculty (the two business conflicts the contract's `timetable:add` guards
 * against - v2 §4.1). Built once per grid build rather than recomputed per cell. */
private data class CellConflict(val faculty: Boolean, val room: Boolean) {
    val any: Boolean get() = faculty || room
}

private fun conflictOf(entries: List<TimetableEntry>): CellConflict {
    if (entries.size < 2) return CellConflict(false, false)
    val facultyDup = entries.mapNotNull { it.facultyId }.groupingBy { it }.eachCount().any { it.value > 1 }
    val roomDup = entries.mapNotNull { it.roomId }.groupingBy { it }.eachCount().any { it.value > 1 }
    return CellConflict(facultyDup, roomDup)
}

private fun activeSubstitutionFor(entry: TimetableEntry, substitutions: List<Substitution>): Substitution? =
    substitutions.firstOrNull {
        it.status?.equals("ACTIVE", ignoreCase = true) == true &&
            (it.ttEntryId == entry.id || it.classSessionId == entry.id)
    }

/**
 * The week as a grid: one column per configured working day, one row per period.
 * Desktop/tablet layout (spec §12) - the period column stays outside the
 * horizontal-scroll region so it reads as a sticky first column while the day
 * columns scroll together as one unit, sharing [horizontalScroll] with the day
 * header row above them.
 */
@Composable
fun WeeklyTimetableGrid(
    schedule: WeeklySchedule,
    substitutions: List<Substitution>,
    onCellClick: (List<TimetableEntry>) -> Unit,
    modifier: Modifier = Modifier
) {
    val horizontalScroll = rememberScrollState()

    Column(modifier = modifier.fillMaxSize()) {
        Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
            Box(modifier = Modifier.width(PERIOD_COLUMN_WIDTH))
            Row(modifier = Modifier.horizontalScroll(horizontalScroll)) {
                schedule.days.forEach { day -> DayHeader(day) }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            items(schedule.periods, key = { it.id ?: it.hashCode().toString() }) { period ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    PeriodLabel(period)
                    Row(modifier = Modifier.horizontalScroll(horizontalScroll)) {
                        schedule.days.forEach { day ->
                            val entries = schedule.entriesAt(day, period)
                            GridCell(
                                entries = entries,
                                conflict = conflictOf(entries),
                                isBreak = period.isBreakSlot,
                                isToday = day.isToday,
                                hasSubstitution = entries.any { activeSubstitutionFor(it, substitutions) != null },
                                onClick = { onCellClick(entries) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DayHeader(day: ScheduleDay) {
    Box(
        modifier = Modifier
            .width(DAY_COLUMN_WIDTH)
            .padding(horizontal = 3.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            day.shortLabel,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = if (day.isToday) FontWeight.Bold else FontWeight.SemiBold,
            color = if (day.isToday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun PeriodLabel(period: TimeSlot) {
    Column(
        modifier = Modifier
            .width(PERIOD_COLUMN_WIDTH)
            .height(ROW_HEIGHT)
            .background(MaterialTheme.colorScheme.background)
            .padding(end = 8.dp, top = 8.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            period.slotName ?: "Period",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        period.displayRange()?.let {
            Text(it, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun GridCell(
    entries: List<TimetableEntry>,
    conflict: CellConflict,
    isBreak: Boolean,
    isToday: Boolean,
    hasSubstitution: Boolean,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(10.dp)
    val statusColors = LocalStatusColors.current
    val background = when {
        isBreak -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
        conflict.any -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f)
        entries.isEmpty() -> MaterialTheme.colorScheme.surface
        isToday -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
        else -> MaterialTheme.colorScheme.surface
    }
    val borderColor = when {
        conflict.any -> MaterialTheme.colorScheme.error
        isToday && entries.isNotEmpty() -> MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
        else -> MaterialTheme.colorScheme.outlineVariant
    }

    Box(
        modifier = Modifier
            .width(DAY_COLUMN_WIDTH)
            .height(ROW_HEIGHT)
            .padding(horizontal = 3.dp, vertical = 3.dp)
            .clip(shape)
            .background(background, shape)
            .border(1.dp, borderColor, shape)
    ) {
        when {
            isBreak -> Text(
                "Break",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.Center)
            )

            entries.isEmpty() -> Text(
                "—",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.align(Alignment.Center)
            )

            else -> CellContent(entries, conflict, hasSubstitution, onClick)
        }
    }
}

@Composable
private fun CellContent(
    entries: List<TimetableEntry>,
    conflict: CellConflict,
    hasSubstitution: Boolean,
    onClick: () -> Unit
) {
    val spacing = LocalSpacing.current
    val primary = entries.first()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .clickableCell(onClick)
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(1.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                primary.courseLabel(),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f, fill = false)
            )
            if (hasSubstitution) {
                Icon(
                    Icons.Default.SwapHoriz,
                    contentDescription = "Substitution active",
                    tint = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.size(13.dp)
                )
            }
        }
        Text(
            primary.facultyLabel(),
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            primary.roomLabel(),
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        primary.sessionTypeId?.let {
            Text(
                it,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1
            )
        }

        if (conflict.any) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                Icon(
                    Icons.Default.WarningAmber,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(11.dp)
                )
                Text(
                    if (conflict.faculty) "Faculty conflict" else "Room conflict",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error,
                    maxLines = 1
                )
            }
        } else if (entries.size > 1) {
            Text(
                "+${entries.size - 1} more",
                fontSize = 9.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

/** Plain click modifier without the ripple's default 48dp minimum-size padding
 * fighting the compact cell size. */
private fun Modifier.clickableCell(onClick: () -> Unit): Modifier =
    this.then(
        androidx.compose.foundation.clickable(
            interactionSource = androidx.compose.runtime.remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
            indication = null,
            onClick = onClick
        )
    )

// --- Mobile: stacked day/period list (spec §12) ---

/**
 * Phone layout: a day selector followed by a vertical list of that day's periods,
 * each carrying the same information the grid cell does - not a shrunk grid, a
 * different composition of the same [WeeklySchedule].
 */
@Composable
fun StackedDayTimetable(
    schedule: WeeklySchedule,
    substitutions: List<Substitution>,
    onCellClick: (List<TimetableEntry>) -> Unit,
    modifier: Modifier = Modifier
) {
    if (schedule.days.isEmpty()) return
    var selectedIndex by remember(schedule.days) {
        mutableIntStateOf(schedule.days.indexOfFirst { it.isToday }.coerceAtLeast(0))
    }
    val spacing = LocalSpacing.current
    val day = schedule.days.getOrNull(selectedIndex) ?: schedule.days.first()

    Column(modifier = modifier.fillMaxSize()) {
        LazyRow(
            contentPadding = PaddingValues(horizontal = spacing.lg, vertical = spacing.sm),
            horizontalArrangement = Arrangement.spacedBy(spacing.sm)
        ) {
            items(schedule.days) { d ->
                val index = schedule.days.indexOf(d)
                FilterChip(
                    selected = index == selectedIndex,
                    onClick = { selectedIndex = index },
                    label = {
                        Text(if (d.isToday) "${d.shortLabel} • Today" else d.shortLabel)
                    }
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = spacing.lg, vertical = spacing.sm),
            verticalArrangement = Arrangement.spacedBy(spacing.sm)
        ) {
            items(schedule.periods, key = { it.id ?: it.hashCode().toString() }) { period ->
                val entries = schedule.entriesAt(day, period)
                StackedPeriodRow(
                    period = period,
                    entries = entries,
                    conflict = conflictOf(entries),
                    hasSubstitution = entries.any { activeSubstitutionFor(it, substitutions) != null },
                    onClick = { if (entries.isNotEmpty()) onCellClick(entries) }
                )
            }
        }
    }
}

@Composable
private fun StackedPeriodRow(
    period: TimeSlot,
    entries: List<TimetableEntry>,
    conflict: CellConflict,
    hasSubstitution: Boolean,
    onClick: () -> Unit
) {
    val spacing = LocalSpacing.current

    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
        Column(modifier = Modifier.width(64.dp).padding(top = 14.dp)) {
            Text(period.slotName ?: "Period", style = MaterialTheme.typography.labelSmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
            period.displayRange()?.let { Text(it, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) }
        }

        when {
            period.isBreakSlot -> Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Text(
                    "Break",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(spacing.md)
                )
            }

            entries.isEmpty() -> Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Text(
                    "Free period",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(spacing.md)
                )
            }

            else -> Card(
                onClick = onClick,
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(
                    containerColor = if (conflict.any) {
                        MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.35f)
                    } else {
                        MaterialTheme.colorScheme.surface
                    }
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (conflict.any) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outlineVariant
                ),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Column(modifier = Modifier.padding(spacing.md)) {
                    val primary = entries.first()
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            primary.courseLabel(),
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier.weight(1f, fill = false),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        primary.sessionTypeId?.let {
                            Text(it, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                        if (hasSubstitution) {
                            Icon(
                                Icons.Default.SwapHoriz,
                                contentDescription = "Substitution active",
                                tint = MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                    Text(
                        "${primary.facultyLabel()} · ${primary.roomLabel()}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (conflict.any) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Icon(Icons.Default.WarningAmber, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(12.dp))
                            Text(
                                if (conflict.faculty) "Faculty conflict" else "Room conflict",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    } else if (entries.size > 1) {
                        Text(
                            "+${entries.size - 1} more in this period",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }
    }
}
