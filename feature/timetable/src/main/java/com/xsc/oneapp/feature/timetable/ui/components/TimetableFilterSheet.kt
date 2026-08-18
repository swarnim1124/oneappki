package com.xsc.oneapp.feature.timetable.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.xsc.oneapp.feature.timetable.domain.model.TimeSlot
import com.xsc.oneapp.feature.timetable.domain.model.TimetableFilter
import com.xsc.oneapp.feature.timetable.domain.model.TimetablePerspective
import com.xsc.oneapp.feature.timetable.domain.model.WorkingDay
import com.xsc.sdk.theme.LocalSpacing

/**
 * Filter popover content (spec §4): perspective switch plus every `timetable:view`
 * filter the contract documents (v2 §4.1) - Section, Faculty, Room, Day, Time slot,
 * Term, Academic year, Timetable id. Options come from whatever has already loaded
 * (see [com.xsc.oneapp.feature.timetable.ui.viewmodel.TimetableViewModel.knownEntries])
 * rather than a master-list lookup this module doesn't have (see TimetableNotes.kt) -
 * Section/Faculty/Room/Term/Year show as raw ids for the same reason the grid does.
 */
@Composable
fun TimetableFilterSheet(
    filter: TimetableFilter,
    perspective: TimetablePerspective,
    onPerspectiveChange: (TimetablePerspective) -> Unit,
    workingDays: List<WorkingDay>,
    timeSlots: List<TimeSlot>,
    sectionOptions: List<String>,
    facultyOptions: List<String>,
    roomOptions: List<String>,
    termOptions: List<String>,
    yearOptions: List<String>,
    ttIdOptions: List<String>,
    onFilterChange: (TimetableFilter) -> Unit,
    onClear: () -> Unit,
    onDone: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current

    Column(modifier = modifier.fillMaxWidth().padding(horizontal = spacing.lg, vertical = spacing.md)) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("Filters", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
            TextButton(onClick = onClear) { Text("Clear all") }
        }

        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = spacing.xs))

        Text("Perspective", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth().padding(top = 6.dp, bottom = spacing.lg)) {
            TimetablePerspective.entries.forEachIndexed { index, value ->
                SegmentedButton(
                    selected = perspective == value,
                    onClick = { onPerspectiveChange(value) },
                    shape = SegmentedButtonDefaults.itemShape(index, TimetablePerspective.entries.size)
                ) { Text(value.label()) }
            }
        }

        FilterSection("Day") {
            ChipRow(
                options = workingDays.mapNotNull { it.dayName ?: it.name },
                selected = filter.dayOfWeek,
                onSelect = { onFilterChange(filter.copy(dayOfWeek = if (filter.dayOfWeek == it) null else it)) }
            )
        }

        FilterSection("Time slot") {
            ChipRow(
                options = timeSlots.mapNotNull { it.id },
                labels = timeSlots.associate { (it.id ?: "") to (it.slotName ?: it.displayRange() ?: (it.id ?: "")) },
                selected = filter.timeSlotId,
                onSelect = { onFilterChange(filter.copy(timeSlotId = if (filter.timeSlotId == it) null else it)) }
            )
        }

        FilterSection("Section") {
            ChipRow(
                options = sectionOptions,
                prefix = "Sec ",
                selected = filter.sectionId,
                onSelect = { onFilterChange(filter.copy(sectionId = if (filter.sectionId == it) null else it)) }
            )
        }

        FilterSection("Faculty") {
            ChipRow(
                options = facultyOptions,
                prefix = "Faculty #",
                selected = filter.facultyId,
                onSelect = { onFilterChange(filter.copy(facultyId = if (filter.facultyId == it) null else it)) }
            )
        }

        FilterSection("Room") {
            ChipRow(
                options = roomOptions,
                prefix = "Room #",
                selected = filter.roomId,
                onSelect = { onFilterChange(filter.copy(roomId = if (filter.roomId == it) null else it)) }
            )
        }

        FilterSection("Term") {
            ChipRow(
                options = termOptions,
                prefix = "Term ",
                selected = filter.termId,
                onSelect = { onFilterChange(filter.copy(termId = if (filter.termId == it) null else it)) }
            )
        }

        FilterSection("Academic year") {
            ChipRow(
                options = yearOptions,
                selected = filter.academicYearId,
                onSelect = { onFilterChange(filter.copy(academicYearId = if (filter.academicYearId == it) null else it)) }
            )
        }

        FilterSection("Timetable") {
            ChipRow(
                options = ttIdOptions,
                prefix = "TT #",
                selected = filter.ttId,
                onSelect = { onFilterChange(filter.copy(ttId = if (filter.ttId == it) null else it)) }
            )
        }

        HorizontalDivider(modifier = Modifier.padding(top = spacing.md))
        Row(modifier = Modifier.fillMaxWidth().padding(top = spacing.md), horizontalArrangement = Arrangement.End) {
            TextButton(onClick = onDone) { Text("Done") }
        }
    }
}

@Composable
private fun FilterSection(title: String, content: @Composable () -> Unit) {
    val spacing = LocalSpacing.current
    Column(modifier = Modifier.padding(bottom = spacing.md)) {
        Text(title, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Row(modifier = Modifier.padding(top = 6.dp)) { content() }
    }
}

@Composable
private fun ChipRow(
    options: List<String>,
    selected: String?,
    onSelect: (String) -> Unit,
    prefix: String = "",
    labels: Map<String, String> = emptyMap()
) {
    if (options.isEmpty()) {
        Text("None available yet", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
        return
    }
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.wrapContentWidth(Alignment.Start)) {
        items(options.distinct()) { option ->
            FilterChip(
                selected = selected == option,
                onClick = { onSelect(option) },
                label = { Text(labels[option] ?: "$prefix$option") }
            )
        }
    }
}

private fun TimetablePerspective.label(): String = when (this) {
    TimetablePerspective.SECTION -> "Section"
    TimetablePerspective.FACULTY -> "Faculty"
    TimetablePerspective.ROOM -> "Room"
}
