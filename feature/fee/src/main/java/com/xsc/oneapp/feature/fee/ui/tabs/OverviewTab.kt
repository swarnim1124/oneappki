package com.xsc.oneapp.feature.fee.ui.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MoneyOff
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.xsc.sdk.commonui.record.RecordCard
import com.xsc.sdk.commonui.record.StatusPill

@Composable
fun OverviewTab(
    onNavigateToPaymentsLedger: () -> Unit,
    onNavigateToConcessions: () -> Unit,
    onNavigateToRefunds: () -> Unit,
    onNavigateToPenalties: () -> Unit,
    onNavigateToReports: () -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item { OutstandingBalanceCard() }
        item { DuesBreakdownCard() }
        item { CollectionsOverviewCard() }
        item { QuickActionsCard(onNavigateToPaymentsLedger, onNavigateToConcessions, onNavigateToRefunds, onNavigateToPenalties, onNavigateToReports) }
    }
}

@Composable
private fun OutstandingBalanceCard() {
    Surface(
        color = Color(0xFF1E3A8A), // Deep blue background matching design
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "OUTSTANDING BALANCE",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "$4,250.00",
                style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Next Due Date: Oct 15, 2023",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = { /* TODO: Implement pay now */ },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color(0xFF1E3A8A)
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Text("Pay Now", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.Default.CreditCard, contentDescription = null, modifier = Modifier.size(18.dp))
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            OutlinedButton(
                onClick = { /* TODO: Implement view schedule */ },
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.White
                ),
                border = null,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
            ) {
                Text("View Schedule", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun DuesBreakdownCard() {
    Column {
        Text(
            text = "Dues Breakdown",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        RecordCard(modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                DueItemRow(
                    icon = Icons.Default.School,
                    title = "Tuition Fee - Fall Semester 2023",
                    subtitle = "Due Oct 15, 2023",
                    amount = "$3,500.00",
                    statusText = "Pending",
                    statusColor = Color.Gray,
                    statusBgColor = Color(0xFFF3F4F6),
                    iconBgColor = Color(0xFFF3F4F6)
                )
                
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                
                DueItemRow(
                    icon = Icons.Default.Book,
                    iconTint = Color(0xFFEF4444),
                    title = "Library Late Return Fine",
                    subtitle = "Overdue since Sep 01, 2023",
                    subtitleColor = Color(0xFFEF4444),
                    amount = "$50.00",
                    amountColor = Color(0xFFEF4444),
                    statusText = "Overdue",
                    statusColor = Color(0xFFEF4444),
                    statusBgColor = Color(0xFFFEE2E2),
                    iconBgColor = Color(0xFFFEE2E2)
                )
                
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                
                DueItemRow(
                    icon = Icons.Default.CheckCircle,
                    iconTint = Color(0xFF10B981),
                    title = "Exam Registration Fee",
                    subtitle = "Paid on Aug 15, 2023",
                    amount = "$300.00",
                    amountStrikethrough = true,
                    statusText = "Paid",
                    statusColor = Color(0xFF10B981),
                    statusBgColor = Color(0xFFD1FAE5),
                    iconBgColor = Color(0xFFD1FAE5)
                )
            }
        }
    }
}

@Composable
private fun DueItemRow(
    icon: ImageVector,
    iconTint: Color = MaterialTheme.colorScheme.onSurface,
    title: String,
    subtitle: String,
    subtitleColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    amount: String,
    amountColor: Color = MaterialTheme.colorScheme.onSurface,
    amountStrikethrough: Boolean = false,
    statusText: String,
    statusColor: Color,
    statusBgColor: Color,
    iconBgColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(iconBgColor, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = subtitleColor
                )
            }
        }
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = amount,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    textDecoration = if (amountStrikethrough) androidx.compose.ui.text.style.TextDecoration.LineThrough else null
                ),
                color = amountColor
            )
            Spacer(modifier = Modifier.height(4.dp))
            StatusPill(
                text = statusText,
                tint = statusColor,
                backgroundColor = statusBgColor
            )
        }
    }
}

@Composable
fun StatusPill(
    text: String,
    tint: Color,
    backgroundColor: Color = tint.copy(alpha = 0.1f),
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
        color = tint,
        modifier = modifier
            .background(backgroundColor, CircleShape)
            .padding(horizontal = 8.dp, vertical = 2.dp)
    )
}

@Composable
private fun CollectionsOverviewCard() {
    RecordCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Collections\nOverview",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = MaterialTheme.typography.titleLarge.lineHeight
            )
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(Color(0xFFFEE2E2), CircleShape)
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = Color(0xFFEF4444),
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "45\nDefaulters",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFFEF4444),
                    lineHeight = MaterialTheme.typography.labelSmall.lineHeight
                )
            }
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "85% Collected",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                color = Color(0xFF2563EB)
            )
            Text(
                text = "15% Outstanding",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Progress Bar (85% vs 15%)
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
                    .background(Color(0xFF2563EB))
            )
            Box(
                modifier = Modifier
                    .weight(0.15f)
                    .fillMaxHeight()
                    .background(Color(0xFFEF4444))
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "$1.2M",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "$200K",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun QuickActionsCard(
    onNavigateToPaymentsLedger: () -> Unit,
    onNavigateToConcessions: () -> Unit,
    onNavigateToRefunds: () -> Unit,
    onNavigateToPenalties: () -> Unit,
    onNavigateToReports: () -> Unit
) {
    Column {
        Text(
            text = "Quick Actions",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        RecordCard(modifier = Modifier.fillMaxWidth()) {
            Column {
                QuickActionItem(
                    icon = Icons.Default.Download,
                    title = "Download Statement",
                    onClick = { /* TODO */ }
                )
                
                QuickActionItem(
                    icon = Icons.Default.History,
                    title = "View History",
                    onClick = onNavigateToPaymentsLedger
                )
                
                QuickActionItem(
                    icon = Icons.Default.Payment,
                    title = "Manage Payment Methods",
                    onClick = { /* TODO */ },
                    showDivider = true
                )
                
                QuickActionItem(
                    icon = Icons.Default.MoneyOff,
                    title = "Manage Concessions",
                    onClick = onNavigateToConcessions,
                    showDivider = true
                )
                
                QuickActionItem(
                    icon = Icons.Default.CreditCard,
                    title = "Manage Refunds",
                    onClick = onNavigateToRefunds,
                    showDivider = true
                )
                
                QuickActionItem(
                    icon = Icons.Default.Gavel,
                    title = "Penalties & Rules",
                    onClick = onNavigateToPenalties,
                    showDivider = true
                )
                
                QuickActionItem(
                    icon = Icons.Default.Analytics,
                    title = "Reports Overview",
                    onClick = onNavigateToReports,
                    showDivider = false
                )
            }
        }
    }
}

@Composable
private fun QuickActionItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    showDivider: Boolean = true
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF2563EB),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.size(20.dp)
            )
        }
        
        if (showDivider) {
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                modifier = Modifier.padding(start = 40.dp)
            )
        }
    }
}
