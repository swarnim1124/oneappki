package com.xsc.oneapp.feature.fee.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeesRefundsScreen(
    onBack: () -> Unit
) {
    var showNewRequestModal by remember { mutableStateOf(false) }

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
                PageHeader(onNewRequestClick = { showNewRequestModal = true })
            }
            
            item {
                MetricsGrid()
            }
            
            item {
                RefundsList()
            }
            
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
    
    if (showNewRequestModal) {
        NewRefundRequestModal(onDismiss = { showNewRequestModal = false })
    }
}

@Composable
private fun PageHeader(onNewRequestClick: () -> Unit) {
    Column {
        Text("Fees Refunds", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface)
        Text("Manage and process student fee refund requests.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Filter Toggle
            Row(
                modifier = Modifier
                    .background(Color(0xFFF2F4F6), RoundedCornerShape(8.dp))
                    .border(1.dp, Color(0xFFC4C5D7), RoundedCornerShape(8.dp))
                    .padding(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clickable { /* TODO */ }
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Text("Mine", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Box(
                    modifier = Modifier
                        .background(Color.White, RoundedCornerShape(6.dp))
                        .border(1.dp, Color(0xFFC4C5D7), RoundedCornerShape(6.dp))
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Text("All", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium), color = MaterialTheme.colorScheme.onSurface)
                }
            }
            
            // New Request Button
            Button(
                onClick = onNewRequestClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D4ED8)),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("New Request", style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}

@Composable
private fun MetricsGrid() {
    Column {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            MetricCard("Total Pending", "12", Modifier.weight(1f))
            MetricCard("Approved Today", "4", Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            MetricCard("Amount Pending", "$3,450", Modifier.weight(1f))
            MetricCard("Processing Time", "2.4 days", Modifier.weight(1f))
        }
    }
}

@Composable
private fun MetricCard(title: String, value: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(Color.White, RoundedCornerShape(12.dp))
            .border(1.dp, Color(0xFFC4C5D7), RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Column {
            Text(title, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold), color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
private fun RefundsList() {
    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
        // Card 1: Requested
        RefundCard(
            name = "Eleanor Vance",
            refId = "REF-2023-8821",
            status = "Requested",
            statusColor = Color(0xFF4F5D6E),
            statusBgColor = Color(0xFFC7D6E9), // tertiary container
            amount = "$450.00",
            date = "Oct 24, 2023",
            reason = "Overpayment on Fall Semester tuition fees due to scholarship application delay.",
            accentColor = Color(0xFF4F5D6E)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF2F4F6))
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = { }) {
                    Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(onClick = { }) {
                    Text("Reject", color = Color(0xFFBA1A1A))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D4ED8)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Approve")
                }
            }
        }
        
        // Card 2: Approved
        RefundCard(
            name = "Marcus Reed",
            refId = "REF-2023-8815",
            status = "Approved",
            statusColor = Color(0xFF1D3989),
            statusBgColor = Color(0xFF8FA7FE), // secondary container
            amount = "$125.50",
            date = "Oct 22, 2023",
            reason = "Duplicate payment for library fines."
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF2F4F6))
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(
                    onClick = { },
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1D4ED8)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Process Payment", color = Color(0xFF1D4ED8))
                }
            }
        }
        
        // Card 3: Processed
        Box(modifier = Modifier.padding(bottom = 24.dp)) {
            RefundCard(
                name = "Sophia Chen",
                refId = "REF-2023-8799",
                status = "Processed",
                statusColor = Color(0xFF434655),
                statusBgColor = Color(0xFFE0E3E5), // surface variant
                amount = "$850.00",
                date = "Oct 18, 2023",
                reason = "Course cancellation before census date.",
                isProcessed = true
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF2F4F6))
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Settled via Stripe", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
private fun RefundCard(
    name: String,
    refId: String,
    status: String,
    statusColor: Color,
    statusBgColor: Color,
    amount: String,
    date: String,
    reason: String,
    accentColor: Color? = null,
    isProcessed: Boolean = false,
    footer: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .border(1.dp, Color(0xFFC4C5D7), RoundedCornerShape(12.dp))
    ) {
        Column {
            // Accent bar for Requested state
            if (accentColor != null) {
                Box(modifier = Modifier.fillMaxWidth().height(4.dp).background(accentColor))
            }
            
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .then(if (isProcessed) Modifier.padding(bottom = 0.dp) else Modifier) // Opacity emulation for processed? The design says opacity 80%
            ) {
                // Header
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                    Column {
                        Text(name, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold), color = MaterialTheme.colorScheme.onSurface)
                        Text(refId, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Box(
                        modifier = Modifier
                            .background(statusBgColor.copy(alpha = 0.3f), CircleShape)
                            .border(1.dp, statusBgColor.copy(alpha = 0.3f), CircleShape)
                            .padding(horizontal = 10.dp, vertical = 2.dp)
                    ) {
                        Text(status, style = MaterialTheme.typography.labelSmall, color = statusColor)
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = Color(0xFFF1F5F9))
                Spacer(modifier = Modifier.height(16.dp))
                
                // Amount & Date
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text("Amount", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(amount, style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold), color = if(isProcessed) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Date", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(date, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = Color(0xFFF1F5F9))
                Spacer(modifier = Modifier.height(16.dp))
                
                // Reason
                Column {
                    Text("Reason", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = reason,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if(isProcessed) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            
            HorizontalDivider(color = Color(0xFFF1F5F9))
            // Footer (Actions)
            footer()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewRefundRequestModal(onDismiss: () -> Unit) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("New Refund Request", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface)
                IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            HorizontalDivider(color = Color(0xFFC4C5D7))
            
            // Body (Scrollable)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Student
                Column {
                    Text("Student ID or Name", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = "",
                        onValueChange = {},
                        placeholder = { Text("Search student...") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color(0xFFC4C5D7)
                        )
                    )
                }
                
                // Original Payment Method
                Column {
                    Text("Original Payment Method", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = "Credit Card ending in 4242",
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color(0xFFC4C5D7)
                        )
                    )
                }
                
                // Refund Amount
                Column {
                    Text("Refund Amount", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = "",
                        onValueChange = {},
                        placeholder = { Text("0.00") },
                        leadingIcon = { Text("$", color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(start = 16.dp, end = 8.dp)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color(0xFFC4C5D7)
                        )
                    )
                }
                
                // Reason for Refund
                Column {
                    Text("Reason for Refund", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = "",
                        onValueChange = {},
                        placeholder = { Text("Provide details...") },
                        modifier = Modifier.fillMaxWidth().height(100.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color(0xFFC4C5D7)
                        ),
                        maxLines = 3
                    )
                }
            }
            
            HorizontalDivider(color = Color(0xFFC4C5D7))
            
            // Footer
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF2F4F6))
                    .padding(24.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onDismiss) {
                    Text("Cancel", color = Color(0xFF1D4ED8))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D4ED8)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Submit Request")
                }
            }
        }
    }
}
