package com.xsc.oneapp.feature.fee.ui.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.xsc.sdk.commonui.record.RecordCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsOverviewScreen(
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("OneApp ERP", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF7F9FB)) // matches body bg
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                PageHeader()
            }
            
            item {
                GoalVsActualCard()
            }
            
            item {
                CollectionTrendCard()
            }
            
            item {
                BreakdownByProgrammeCard()
            }
            
            item {
                TopDefaultersCard()
            }
            
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun PageHeader() {
    Column {
        Text("Reports Overview", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface)
        Text("Collections & Accounts Receivable Analytics", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun GoalVsActualCard() {
    RecordCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.PieChart, contentDescription = null, tint = Color(0xFF1D4ED8), modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Goal vs Actual", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold), color = MaterialTheme.colorScheme.onSurface)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text("YTD Collections Target", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Donut Chart
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(120.dp)) {
                    val strokeWidth = 12.dp.toPx()
                    // Background track
                    drawArc(
                        color = Color(0xFFE0E3E5), // surface-container-highest
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                    // Progress arc (85% = 306 degrees)
                    drawArc(
                        color = Color(0xFF0037B0), // primary
                        startAngle = -90f,
                        sweepAngle = 306f,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                }
                Text("85%", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface)
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Progress Bar text
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Collected: $4.2M", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Target: $5.0M", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium), color = MaterialTheme.colorScheme.onSurface)
            }
            Spacer(modifier = Modifier.height(8.dp))
            
            // Progress Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(CircleShape)
            ) {
                Box(
                    modifier = Modifier
                        .weight(0.85f)
                        .fillMaxHeight()
                        .background(Color(0xFF0037B0))
                )
                Box(
                    modifier = Modifier
                        .weight(0.15f)
                        .fillMaxHeight()
                        .background(Color(0xFFE0E3E5))
                )
            }
        }
    }
}

@Composable
private fun CollectionTrendCard() {
    RecordCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.BarChart, contentDescription = null, tint = Color(0xFF1D4ED8), modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Collection Trend", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold), color = MaterialTheme.colorScheme.onSurface)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Monthly Inflow (Last 6 Months)", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { /* TODO */ }
                ) {
                    Text("Export", style = MaterialTheme.typography.labelMedium, color = Color(0xFF1D4ED8))
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.Default.Download, contentDescription = null, tint = Color(0xFF1D4ED8), modifier = Modifier.size(16.dp))
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Bar Chart (Simplified using Boxes)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                ChartBar(heightFraction = 0.4f, label = "Jan")
                ChartBar(heightFraction = 0.55f, label = "Feb")
                ChartBar(heightFraction = 0.3f, label = "Mar")
                ChartBar(heightFraction = 0.8f, label = "Apr", isHighlighted = true)
                ChartBar(heightFraction = 0.65f, label = "May")
                ChartBar(heightFraction = 0.5f, label = "Jun")
            }
        }
    }
}

@Composable
private fun ChartBar(heightFraction: Float, label: String, isHighlighted: Boolean = false) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier.fillMaxHeight()
    ) {
        if (isHighlighted) {
            Text("$2.1M", style = MaterialTheme.typography.labelSmall, color = Color(0xFF0037B0))
            Spacer(modifier = Modifier.height(4.dp))
        }
        
        Box(
            modifier = Modifier
                .width(40.dp) // responsive bar width
                .fillMaxHeight(heightFraction)
                .background(
                    if (isHighlighted) Color(0xFF0037B0) else Color(0xFFE0E3E5),
                    RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
                )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            label,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = if (isHighlighted) FontWeight.Bold else FontWeight.Normal),
            color = if (isHighlighted) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun BreakdownByProgrammeCard() {
    RecordCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Category, contentDescription = null, tint = Color(0xFF1D4ED8), modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Breakdown by Programme", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold), color = MaterialTheme.colorScheme.onSurface)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text("Collection distribution across departments", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Items
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                BreakdownItem("B.Tech", "$1.8M (92%)", 0.92f, Color(0xFF0037B0))
                BreakdownItem("MBA", "$1.2M (78%)", 0.78f, Color(0xFF4059AA))
                BreakdownItem("B.Sc", "$0.8M (65%)", 0.65f, Color(0xFF374655))
                BreakdownItem("Ph.D", "$0.4M (95%)", 0.95f, Color(0xFF747686))
            }
        }
    }
}

@Composable
private fun BreakdownItem(title: String, stat: String, progress: Float, progressColor: Color) {
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
            Text(title, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium), color = MaterialTheme.colorScheme.onSurface)
            Text(stat, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(CircleShape)
        ) {
            Box(
                modifier = Modifier
                    .weight(progress)
                    .fillMaxHeight()
                    .background(progressColor)
            )
            Box(
                modifier = Modifier
                    .weight(1f - progress)
                    .fillMaxHeight()
                    .background(Color(0xFFE0E3E5)) // surface-container-highest
            )
        }
    }
}

@Composable
private fun TopDefaultersCard() {
    RecordCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFBA1A1A), modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Top Defaulters", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold), color = MaterialTheme.colorScheme.onSurface)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("High priority outstanding accounts", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                
                Text("View All", style = MaterialTheme.typography.labelMedium, color = Color(0xFF1D4ED8), modifier = Modifier.clickable { /* TODO */ })
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = Color(0xFFC4C5D7).copy(alpha = 0.3f))
            
            // List
            DefaulterRow(initials = "SJ", name = "Sarah Jenkins", desc = "B.Tech • 90 Days Overdue", amount = "$4,500", initialsBg = Color(0xFFFFDAD6), initialsColor = Color(0xFF93000A), amountColor = Color(0xFFBA1A1A))
            HorizontalDivider(color = Color(0xFFC4C5D7).copy(alpha = 0.3f))
            
            DefaulterRow(initials = "MC", name = "Michael Chang", desc = "MBA • 60 Days Overdue", amount = "$3,200", initialsBg = Color(0xFFE6E8EA), initialsColor = Color(0xFF191C1E), amountColor = MaterialTheme.colorScheme.onSurface)
            HorizontalDivider(color = Color(0xFFC4C5D7).copy(alpha = 0.3f))
            
            DefaulterRow(initials = "AL", name = "Anita Lopez", desc = "B.Sc • 45 Days Overdue", amount = "$2,800", initialsBg = Color(0xFFE6E8EA), initialsColor = Color(0xFF191C1E), amountColor = MaterialTheme.colorScheme.onSurface)
            HorizontalDivider(color = Color(0xFFC4C5D7).copy(alpha = 0.3f))
            
            DefaulterRow(initials = "DT", name = "David Thompson", desc = "Ph.D • 30 Days Overdue", amount = "$1,500", initialsBg = Color(0xFFE6E8EA), initialsColor = Color(0xFF191C1E), amountColor = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
private fun DefaulterRow(initials: String, name: String, desc: String, amount: String, initialsBg: Color, initialsColor: Color, amountColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* TODO */ }
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(initialsBg, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(initials, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = initialsColor)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(name, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium), color = MaterialTheme.colorScheme.onSurface)
                Text(desc, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(amount, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold), color = amountColor)
            Spacer(modifier = Modifier.width(16.dp))
            Icon(Icons.Default.Mail, contentDescription = "Send Reminder", tint = Color(0xFF1D4ED8), modifier = Modifier.size(20.dp))
        }
    }
}
