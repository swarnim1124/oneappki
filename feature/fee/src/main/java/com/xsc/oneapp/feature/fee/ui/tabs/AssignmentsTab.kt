package com.xsc.oneapp.feature.fee.ui.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.xsc.sdk.commonui.record.RecordCard

@Composable
fun AssignmentsTab() {
    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth().weight(1f)
        ) {
            item {
                AssignmentsFilterCard()
            }
            
            item {
                AssignmentCard(
                    initials = "JD",
                    initialsBgColor = Color(0xFFDBEAFE),
                    initialsColor = Color(0xFF1E3A8A),
                    name = "John Doe",
                    id = "ID: STU-2024-001",
                    feeType = "Tuition - Fall",
                    amount = "$4,500.00",
                    statusText = "ASSIGNED",
                    statusColor = Color(0xFF2563EB),
                    statusBgColor = Color(0xFFDBEAFE),
                    dueText = "Due: Oct 15",
                    dueColor = Color(0xFF2563EB),
                    showNotifyBlock = true,
                    showReceipt = false
                )
            }
            item {
                AssignmentCard(
                    initials = "SS",
                    initialsBgColor = Color(0xFFE5E7EB),
                    initialsColor = Color(0xFF374151),
                    name = "Sarah Smith",
                    id = "ID: STU-2024-042",
                    feeType = "Lab Equipment",
                    amount = "$350.00",
                    statusText = "OVERDUE",
                    statusColor = Color(0xFFEF4444),
                    statusBgColor = Color(0xFFFEE2E2),
                    dueText = "Due: Sep 01",
                    dueColor = Color(0xFFEF4444),
                    showNotifyBlock = true,
                    showReceipt = false
                )
            }
            item {
                AssignmentCard(
                    initials = "MJ",
                    initialsBgColor = Color(0xFFF3F4F6),
                    initialsColor = Color(0xFF4B5563),
                    name = "Michael Johnson",
                    id = "ID: STU-2023-112",
                    feeType = "Tuition - Fall",
                    amount = "$4,500.00",
                    statusText = "PAID",
                    statusColor = Color(0xFF10B981),
                    statusBgColor = Color(0xFFD1FAE5),
                    dueText = "Paid: Oct 02",
                    dueColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    showNotifyBlock = false,
                    showReceipt = true,
                    dueIcon = Icons.Default.CheckCircle // Wait, there's a check mark in the paid pill
                )
            }
        }
    }
}

@Composable
private fun AssignmentsFilterCard() {
    RecordCard(modifier = Modifier.fillMaxWidth()) {
        Column {
            Text("Term", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedDropdown("Fall 2024")
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text("Programme", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedDropdown("All Programmes")
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = false, onCheckedChange = { /* TODO */ })
                Text("Overdue Only", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = { /* TODO */ },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFDBEAFE),
                        contentColor = Color(0xFF1E3A8A)
                    ),
                    modifier = Modifier.height(40.dp)
                ) {
                    Text("Select Cohort", fontWeight = FontWeight.Medium)
                }
                
                OutlinedButton(
                    onClick = { /* TODO */ },
                    contentPadding = PaddingValues(horizontal = 12.dp),
                    modifier = Modifier.height(40.dp)
                ) {
                    Text("Bulk Actions", color = MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface)
                }
            }
        }
    }
}

@Composable
private fun OutlinedDropdown(value: String) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        color = Color.Transparent,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 10.dp)
                .clickable { /* TODO: Show dropdown */ },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(value, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun AssignmentCard(
    initials: String,
    initialsBgColor: Color,
    initialsColor: Color,
    name: String,
    id: String,
    feeType: String,
    amount: String,
    statusText: String,
    statusColor: Color,
    statusBgColor: Color,
    dueText: String,
    dueColor: Color,
    showNotifyBlock: Boolean,
    showReceipt: Boolean,
    dueIcon: ImageVector? = null
) {
    var checked by remember { mutableStateOf(false) }
    
    RecordCard(modifier = Modifier.fillMaxWidth()) {
        Column {
            Row(verticalAlignment = Alignment.Top) {
                Checkbox(
                    checked = checked,
                    onCheckedChange = { checked = it },
                    modifier = Modifier.padding(end = 8.dp).size(20.dp)
                )
                
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(initialsBgColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    // For Sarah Smith we'd ideally load an image, but using initials for now
                    Text(initials, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = initialsColor)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = id,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Fee Type",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = feeType,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "Amount",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = amount,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .background(statusBgColor, RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (dueIcon != null) {
                            Icon(dueIcon, contentDescription = null, tint = statusColor, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        Text(
                            text = statusText,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = statusColor
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = dueText,
                        style = MaterialTheme.typography.labelSmall,
                        color = dueColor
                    )
                }
                
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    if (showNotifyBlock) {
                        IconButton(onClick = { /* TODO */ }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.NotificationsNone, contentDescription = "Notify", tint = MaterialTheme.colorScheme.onSurface)
                        }
                        IconButton(onClick = { /* TODO */ }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Block, contentDescription = "Block", tint = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                    if (showReceipt) {
                        IconButton(onClick = { /* TODO */ }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Receipt, contentDescription = "Receipt", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}
