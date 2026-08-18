package com.xsc.oneapp.feature.timetable.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.xsc.sdk.theme.LocalSpacing

/** Loading state (spec §14) - a skeleton shaped like the grid it's about to become,
 * not a spinner over blank space. One shared pulse drives every block so the whole
 * grid breathes together rather than each cell animating out of phase. */
@Composable
fun TimetableGridSkeleton(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "timetable-skeleton")
    val alpha by transition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(tween(900), repeatMode = RepeatMode.Reverse),
        label = "alpha"
    )
    val spacing = LocalSpacing.current
    val tone = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = alpha)

    Column(modifier = modifier.fillMaxSize().padding(spacing.lg)) {
        Row(horizontalArrangement = Arrangement.spacedBy(spacing.sm), modifier = Modifier.fillMaxWidth().padding(bottom = spacing.md)) {
            SkeletonBlock(Modifier.weight(0.6f).height(20.dp), tone)
            SkeletonBlock(Modifier.weight(0.3f).height(20.dp), tone)
        }
        repeat(6) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = spacing.sm),
                horizontalArrangement = Arrangement.spacedBy(spacing.sm)
            ) {
                SkeletonBlock(Modifier.weight(0.12f).height(64.dp), tone)
                repeat(5) { SkeletonBlock(Modifier.weight(0.18f).height(64.dp), tone) }
            }
        }
    }
}

@Composable
private fun SkeletonBlock(modifier: Modifier, tone: androidx.compose.ui.graphics.Color) {
    Box(modifier = modifier.fillMaxWidth().background(tone, RoundedCornerShape(8.dp)))
}
