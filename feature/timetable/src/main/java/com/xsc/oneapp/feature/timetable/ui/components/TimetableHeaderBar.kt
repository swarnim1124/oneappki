package com.xsc.oneapp.feature.timetable.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xsc.oneapp.feature.timetable.domain.model.TimetableHeaderInfo
import com.xsc.sdk.theme.LocalSpacing

/**
 * The compact header (spec §1): title + metadata badges + a subtle status pill,
 * search/filter/export/more on the right. Deliberately not a big colored banner -
 * status is a small pill, not a component of its own.
 */
@Composable
fun TimetableHeaderBar(
    header: TimetableHeaderInfo?,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    activeFilterCount: Int,
    onFilterClick: () -> Unit,
    canExport: Boolean,
    onExportClick: () -> Unit,
    onRefreshClick: () -> Unit,
    onOverviewClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current
    var searchExpanded by remember { mutableStateOf(false) }
    var moreMenuExpanded by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth().padding(horizontal = spacing.screenHorizontal, vertical = spacing.md)) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(spacing.sm)) {
                    Text("Timetable", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onSurface)
                    header?.status?.let { StatusBadge(it) }
                }
                if (header != null) {
                    Text(
                        headerSubtitle(header),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { searchExpanded = !searchExpanded }) {
                    Icon(
                        if (searchExpanded) Icons.Default.Close else Icons.Default.Search,
                        contentDescription = if (searchExpanded) "Close search" else "Search"
                    )
                }
                Box {
                    IconButton(onClick = onFilterClick) {
                        Icon(Icons.Default.Tune, contentDescription = "Filter")
                    }
                    if (activeFilterCount > 0) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(top = 6.dp, end = 6.dp)
                                .background(MaterialTheme.colorScheme.error, CircleShape)
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text(
                                activeFilterCount.toString(),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onError
                            )
                        }
                    }
                }
                if (canExport) {
                    IconButton(onClick = onExportClick) {
                        Icon(Icons.Default.FileDownload, contentDescription = "Export")
                    }
                }
                Box {
                    IconButton(onClick = { moreMenuExpanded = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More actions")
                    }
                    DropdownMenu(expanded = moreMenuExpanded, onDismissRequest = { moreMenuExpanded = false }) {
                        DropdownMenuItem(
                            text = { Text("Refresh") },
                            leadingIcon = { Icon(Icons.Default.Refresh, contentDescription = null) },
                            onClick = { moreMenuExpanded = false; onRefreshClick() }
                        )
                        DropdownMenuItem(
                            text = { Text("Allocations & approval") },
                            leadingIcon = { Icon(Icons.Default.Groups, contentDescription = null) },
                            onClick = { moreMenuExpanded = false; onOverviewClick() }
                        )
                    }
                }
            }
        }

        AnimatedVisibility(visible = searchExpanded) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = { Text("Search course, faculty, room, session type…") },
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth().padding(top = spacing.sm)
            )
        }

        if (header != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(top = spacing.sm),
                horizontalArrangement = Arrangement.spacedBy(spacing.sm)
            ) {
                header.ttCode?.let { MetaChip("Code", it) }
                header.academicYearId?.let { MetaChip("Year", it) }
                header.termId?.let { MetaChip("Term", it) }
                header.sectionId?.let { MetaChip("Section", it) }
                header.versionNo?.let { MetaChip("v.", it) }
                if (header.effectiveFrom != null || header.effectiveTo != null) {
                    MetaChip("Effective", listOfNotNull(header.effectiveFrom, header.effectiveTo).joinToString(" – "))
                }
                if (header.isActive == false) {
                    MetaChip("State", "Inactive")
                }
            }
        }
    }
}

private fun headerSubtitle(header: TimetableHeaderInfo): String =
    listOfNotNull(
        header.academicYearId?.let { "AY $it" },
        header.termId?.let { "Term $it" },
        header.sectionId?.let { "Section $it" }
    ).joinToString(" · ").ifBlank { "—" }

@Composable
private fun StatusBadge(status: String) {
    val color = timetableStatusColor(status)
    Text(
        status.statusLabel(),
        style = MaterialTheme.typography.labelSmall,
        color = color,
        modifier = Modifier
            .background(color.copy(alpha = 0.12f), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    )
}

@Composable
private fun MetaChip(label: String, value: String) {
    Row(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
    }
}
