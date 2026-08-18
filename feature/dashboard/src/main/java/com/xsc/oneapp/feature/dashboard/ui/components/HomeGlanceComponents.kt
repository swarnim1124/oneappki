package com.xsc.oneapp.feature.dashboard.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.xsc.oneapp.core.dashboard.DashboardTimelinePoint
import com.xsc.oneapp.feature.dashboard.domain.model.DashboardStat
import com.xsc.oneapp.feature.dashboard.domain.model.ModuleItem
import com.xsc.oneapp.feature.dashboard.domain.model.NotificationItem
import com.xsc.sdk.theme.LocalSpacing

fun getIconForNotification(iconName: String): ImageVector {
    return when (iconName) {
        "ic_check_circle" -> Icons.Default.CheckCircle
        "ic_school" -> Icons.Default.School
        "ic_event_busy" -> Icons.Default.EventBusy
        "ic_library" -> Icons.AutoMirrored.Filled.MenuBook
        "ic_document" -> Icons.Default.Description
        "ic_clock" -> Icons.Default.EventAvailable
        "ic_rupee" -> Icons.Default.CurrencyRupee
        else -> Icons.Default.Notifications
    }
}

/**
 * Home tab "Day at a Glance" - fed by the same `state.stats` list the previous
 * "Overview" row used (see DashboardViewModel.getMockStats). "attendance" is real
 * once the shortage-report call resolves (AttendanceDashboardStatProvider);
 * "nextclass" is real once the day's schedule resolves (TimetableDashboardStatProvider).
 * Both fall back to the Dashboard's own "Coming Soon" placeholder until then - this
 * composable only ever renders whatever DashboardStat it's handed, never its own data.
 */
@Composable
fun DayAtGlanceSection(stats: List<DashboardStat>) {
    val spacing = LocalSpacing.current
    val attendance = stats.firstOrNull { it.id == "attendance" }
    val nextClass = stats.firstOrNull { it.id == "nextclass" }

    if (attendance == null && nextClass == null) return

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(spacing.md)
    ) {
        if (attendance != null) {
            AttendanceGlanceCard(stat = attendance, modifier = Modifier.weight(1f))
        }
        if (nextClass != null) {
            NextClassGlanceCard(stat = nextClass, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun AttendanceGlanceCard(stat: DashboardStat, modifier: Modifier = Modifier) {
    val spacing = LocalSpacing.current
    val atRisk = stat.tag.contains("risk", ignoreCase = true) || stat.tag.contains("shortage", ignoreCase = true)
    val badgeBg = if (atRisk) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primaryContainer
    val badgeFg = if (atRisk) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onPrimaryContainer

    Card(
        modifier = modifier.heightIn(min = 132.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(spacing.lg),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier.size(32.dp).background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.EventAvailable,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Text(
                    text = stat.tag,
                    style = MaterialTheme.typography.labelSmall,
                    color = badgeFg,
                    modifier = Modifier
                        .background(badgeBg, MaterialTheme.shapes.extraLarge)
                        .padding(horizontal = spacing.sm, vertical = 3.dp)
                )
            }
            Column {
                Text(stat.value, style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onSurface)
                Text(
                    stat.title,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun NextClassGlanceCard(stat: DashboardStat, modifier: Modifier = Modifier) {
    val spacing = LocalSpacing.current

    Card(
        modifier = modifier.heightIn(min = 132.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(spacing.lg),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(32.dp).background(MaterialTheme.colorScheme.surfaceVariant, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Schedule,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(spacing.sm))
                Text(stat.title, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Column {
                Text(
                    stat.value,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    stat.tag,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/**
 * Home tab "Today's Timeline" - fed by [DashboardState.todayTimeline]
 * ([com.xsc.oneapp.core.dashboard.DashboardTimelineProvider], real today via
 * TimetableTimelineProvider). Renders nothing when [points] is empty (no business
 * module has today's schedule) rather than falling back to fabricated points - this
 * card used to always render 4 hardcoded points regardless of the day or who was
 * signed in.
 */
@Composable
fun TodayTimelineCard(points: List<DashboardTimelinePoint>) {
    if (points.isEmpty()) return
    val spacing = LocalSpacing.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(spacing.lg)) {
            Text(
                text = "TODAY'S TIMELINE",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(spacing.lg))
            Box(modifier = Modifier.fillMaxWidth()) {
                HorizontalDivider(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .fillMaxWidth()
                        .padding(horizontal = 6.dp),
                    thickness = 2.dp,
                    color = MaterialTheme.colorScheme.outlineVariant
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    points.forEach { point -> TimelineDot(point.state) }
                }
            }
            Spacer(modifier = Modifier.height(spacing.sm))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                points.forEach { point ->
                    Text(
                        text = point.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (point.state == DashboardTimelinePoint.State.UPCOMING) {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        } else {
                            MaterialTheme.colorScheme.primary
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun TimelineDot(state: DashboardTimelinePoint.State) {
    when (state) {
        DashboardTimelinePoint.State.DONE -> Box(
            modifier = Modifier.size(12.dp).background(MaterialTheme.colorScheme.primary, CircleShape)
        )
        DashboardTimelinePoint.State.CURRENT -> Box(
            modifier = Modifier
                .size(12.dp)
                .background(MaterialTheme.colorScheme.surface, CircleShape)
                .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
        )
        DashboardTimelinePoint.State.UPCOMING -> Box(
            modifier = Modifier.size(10.dp).background(MaterialTheme.colorScheme.outlineVariant, CircleShape)
        )
    }
}

/**
 * Home tab "Your Workspace" - the same `modules` / `pinnedIds` DashboardScreen already
 * threads through PinnedInfoSection and PinModulesDialog, just rendered as an icon
 * grid instead of a card list. The 8th tile ("More") isn't a module - it switches to
 * the Modules tab, reusing the existing setTab(DashboardTab.CURRICULUM) navigation.
 *
 * [hasFeesAlert] mirrors ActionsFeedSection's Pending Fees card: both key off the
 * Dashboard's "fees" stat (FeeDashboardStatProvider, :feature:fee), so the red dot on
 * the Fees tile only shows when there is an actual outstanding balance.
 */
@Composable
fun WorkspaceCard(
    modules: List<ModuleItem>,
    pinnedIds: Set<String>,
    hasFeesAlert: Boolean,
    onModuleClick: (ModuleItem) -> Unit,
    onMoreClick: () -> Unit
) {
    val spacing = LocalSpacing.current
    val tiles = modules.take(7)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(spacing.lg),
            verticalArrangement = Arrangement.spacedBy(spacing.lg)
        ) {
            (tiles.map { WorkspaceEntry.Module(it) } + WorkspaceEntry.More)
                .chunked(4)
                .forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(spacing.sm)
                    ) {
                        row.forEach { entry ->
                            when (entry) {
                                is WorkspaceEntry.Module -> WorkspaceTile(
                                    modifier = Modifier.weight(1f),
                                    icon = getIconForModule(entry.module.icon),
                                    label = entry.module.displayName,
                                    tint = entry.module.accentColor,
                                    showPin = entry.module.id in pinnedIds,
                                    showAlertDot = hasFeesAlert && entry.module.id == "fees",
                                    onClick = { onModuleClick(entry.module) }
                                )
                                WorkspaceEntry.More -> WorkspaceTile(
                                    modifier = Modifier.weight(1f),
                                    icon = Icons.Default.MoreHoriz,
                                    label = "More",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    showPin = false,
                                    showAlertDot = false,
                                    onClick = onMoreClick
                                )
                            }
                        }
                        // Pads a ragged last row so tiles keep a consistent width
                        // instead of stretching wider than the rows above it.
                        repeat(4 - row.size) { Spacer(modifier = Modifier.weight(1f)) }
                    }
                }
        }
    }
}

private sealed class WorkspaceEntry {
    data class Module(val module: ModuleItem) : WorkspaceEntry()
    object More : WorkspaceEntry()
}

@Composable
private fun WorkspaceTile(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    tint: Color,
    showPin: Boolean,
    showAlertDot: Boolean,
    onClick: () -> Unit
) {
    val spacing = LocalSpacing.current

    Column(
        modifier = modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(tint.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(22.dp))
            }
            if (showPin) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 4.dp, y = (-4).dp)
                        .size(16.dp)
                        .background(MaterialTheme.colorScheme.surface, CircleShape)
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.PushPin,
                        contentDescription = "Pinned",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(9.dp)
                    )
                }
            }
            if (showAlertDot) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 2.dp, y = (-2).dp)
                        .size(9.dp)
                        .background(MaterialTheme.colorScheme.error, CircleShape)
                        .border(1.dp, MaterialTheme.colorScheme.surface, CircleShape)
                )
            }
        }
        Spacer(modifier = Modifier.height(spacing.xs))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/**
 * Home tab "Actions & Feed".
 *
 * [feesStat] is the Dashboard's "fees" stat (see DashboardState.stats /
 * FeeDashboardStatProvider in :feature:fee) - the same outstanding balance the Fees
 * module itself reads, not a card-local number. "Pay Now" is real navigation
 * (onPayFees -> the existing /fees route).
 *
 * [recentActivity] is the first few real rows from
 * [com.xsc.oneapp.feature.dashboard.domain.repository.NotificationRepository] (empty
 * until backend defines an activity-feed endpoint - see that interface's doc
 * comment), not a static list. "View All Activity" is real navigation (switches to
 * the Alerts tab).
 */
@Composable
fun ActionsFeedSection(
    feesStat: DashboardStat?,
    recentActivity: List<NotificationItem>,
    onPayFees: () -> Unit,
    onViewAllActivity: () -> Unit
) {
    val spacing = LocalSpacing.current

    Column(verticalArrangement = Arrangement.spacedBy(spacing.lg)) {
        PendingFeesCard(feesStat = feesStat, onPayFees = onPayFees)

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column {
                recentActivity.forEachIndexed { index, item ->
                    FeedItemRow(item = item)
                    if (index != recentActivity.lastIndex) {
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outlineVariant,
                            modifier = Modifier.padding(start = 68.dp)
                        )
                    }
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                androidx.compose.material3.TextButton(
                    onClick = onViewAllActivity,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("View All Activity", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}

/**
 * Reads [feesStat] rather than owning any fee data itself - see
 * FeeDashboardStatProvider (:feature:fee) for where the real outstanding balance
 * comes from. Three states, matching that provider's contract:
 *  - `null`, or its placeholder "Coming Soon" tag: nothing has resolved yet (no
 *    session, still loading, or the call failed) - shown neutrally, no amount
 *    invented.
 *  - [FeeDashboardTags.DUE]: a real amount is owed - the amount and "Pay Now" render.
 *  - [FeeDashboardTags.PAID]: the backend confirmed nothing outstanding - a plain
 *    positive state, no warning styling and no button.
 */
@Composable
private fun PendingFeesCard(feesStat: DashboardStat?, onPayFees: () -> Unit) {
    val spacing = LocalSpacing.current
    val hasDue = feesStat?.tag == FeeDashboardTags.DUE
    val allPaid = feesStat?.tag == FeeDashboardTags.PAID

    val containerColor = when {
        hasDue -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
        allPaid -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(modifier = Modifier.padding(spacing.lg), verticalAlignment = Alignment.Top) {
            Icon(
                if (hasDue) Icons.Default.WarningAmber else Icons.Default.CurrencyRupee,
                contentDescription = null,
                tint = if (hasDue) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(spacing.md))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    when {
                        hasDue -> "Pending Fees: ${feesStat?.value}"
                        allPaid -> "Fees: all paid up"
                        else -> "Fee status"
                    },
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    when {
                        hasDue -> "Pay online any time before the due date on your statement"
                        allPaid -> "Nothing outstanding right now"
                        else -> "Coming soon"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = if (hasDue) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (hasDue) {
                    Spacer(modifier = Modifier.height(spacing.sm))
                    Button(
                        onClick = onPayFees,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onError
                        ),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = spacing.lg, vertical = spacing.sm),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                    ) {
                        Text("Pay Now", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        }
    }
}

/** Mirrors the tag strings [com.xsc.oneapp.feature.fee.dashboard.FeeDashboardStatProvider]
 * sets on the "fees" stat - kept here (rather than importing across the module
 * boundary, which :feature:dashboard deliberately doesn't have) since :core's
 * [com.xsc.oneapp.core.dashboard.DashboardStatContribution] only carries plain
 * strings by design. */
private object FeeDashboardTags {
    const val DUE = "Due now"
    const val PAID = "All paid"
}

@Composable
private fun FeedItemRow(item: NotificationItem) {
    val spacing = LocalSpacing.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = spacing.lg, vertical = spacing.md),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                getIconForNotification(item.icon),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.width(spacing.md))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                item.title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                item.timestamp,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
