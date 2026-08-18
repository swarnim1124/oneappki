package com.xsc.oneapp.feature.fee.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.xsc.sdk.commonui.record.RecordCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConcessionsQueueScreen(
    onBack: () -> Unit
) {
    var showGrantModal by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Concessions Queue") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF7F9FB)) // matches body bg
                .padding(padding)
        ) {
            // Header Content
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Review and manage student fee concessions and financial aid requests.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = { /* TODO */ },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp)
                    ) {
                        Icon(Icons.Default.FilterList, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Filter", color = MaterialTheme.colorScheme.onSurface)
                    }
                    
                    Button(
                        onClick = { showGrantModal = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D4ED8)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Grant Concession")
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Mobile Tabs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TabPill("Payments", false)
                    TabPill("Concessions", true)
                    TabPill("Refunds", false)
                }
            }
            
            // Table
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                ConcessionsTableCard()
            }
        }
    }
    
    if (showGrantModal) {
        GrantConcessionModal(onDismiss = { showGrantModal = false })
    }
}

@Composable
private fun TabPill(text: String, selected: Boolean) {
    Box(
        modifier = Modifier
            .background(
                color = if (selected) Color(0xFFDBEAFE) else Color.White,
                shape = CircleShape
            )
            .border(
                width = 1.dp,
                color = if (selected) Color.Transparent else MaterialTheme.colorScheme.outlineVariant,
                shape = CircleShape
            )
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { /* TODO */ }
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
            color = if (selected) Color(0xFF1D4ED8) else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ConcessionsTableCard() {
    RecordCard(modifier = Modifier.fillMaxWidth()) {
        Column {
            val scrollState = rememberScrollState()
            Row(modifier = Modifier.horizontalScroll(scrollState)) {
                Column {
                    // Header Row
                    Row(
                        modifier = Modifier
                            .background(Color(0xFFF7F9FB))
                            .border(1.dp, Color(0xFFF1F5F9))
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        TableHeaderCell("STUDENT", 240.dp)
                        TableHeaderCell("FEE TYPE", 200.dp)
                        TableHeaderCell("CONCESSION TYPE", 180.dp)
                        TableHeaderCell("AMOUNT", 100.dp, Alignment.End)
                        TableHeaderCell("STATUS", 120.dp)
                        TableHeaderCell("ACTIONS", 120.dp, Alignment.End)
                    }
                    
                    // Rows
                    TableRow(
                        initials = "AJ",
                        name = "Alex Johnson",
                        id = "ID: STU-2023-1042",
                        feeType = "Tuition Fee - Fall 2024",
                        concessionType = "Financial Aid",
                        amount = "25%",
                        statusText = "Pending",
                        statusColor = Color(0xFF92400E),
                        statusBgColor = Color(0xFFFEF3C7),
                        statusDotColor = Color(0xFFD97706)
                    )
                    
                    HorizontalDivider(color = Color(0xFFF1F5F9))
                    
                    TableRow(
                        initials = "SM",
                        name = "Sarah Miller",
                        id = "ID: STU-2022-0891",
                        feeType = "Library Fee - Annual",
                        concessionType = "Merit Scholarship",
                        amount = "100%",
                        statusText = "Approved",
                        statusColor = Color(0xFF065F46),
                        statusBgColor = Color(0xFFD1FAE5),
                        statusDotColor = Color(0xFF059669)
                    )
                    
                    HorizontalDivider(color = Color(0xFFF1F5F9))
                    
                    TableRow(
                        initials = "DK",
                        name = "David Kim",
                        id = "ID: STU-2024-2105",
                        feeType = "Hostel Fee - Spring 2024",
                        concessionType = "Special Circumstance",
                        amount = "50%",
                        statusText = "Rejected",
                        statusColor = Color(0xFF991B1B),
                        statusBgColor = Color(0xFFFEE2E2),
                        statusDotColor = Color(0xFFDC2626)
                    )
                }
            }
            
            // Pagination Footer
            HorizontalDivider(color = Color(0xFFF1F5F9))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Showing 1 to 3 of 24 entries",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(onClick = {}, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    PageButton("1", true)
                    PageButton("2", false)
                    PageButton("3", false)
                    IconButton(onClick = {}, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
private fun PageButton(text: String, selected: Boolean) {
    Box(
        modifier = Modifier
            .size(24.dp)
            .background(if (selected) Color(0xFFDBEAFE) else Color.Transparent, RoundedCornerShape(4.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
            color = if (selected) Color(0xFF1D4ED8) else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun TableHeaderCell(text: String, width: androidx.compose.ui.unit.Dp, alignment: Alignment.Horizontal = Alignment.Start) {
    Box(
        modifier = Modifier.width(width),
        contentAlignment = when (alignment) {
            Alignment.End -> Alignment.CenterEnd
            Alignment.CenterHorizontally -> Alignment.Center
            else -> Alignment.CenterStart
        }
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            letterSpacing = 1.sp
        )
    }
}

@Composable
private fun TableRow(
    initials: String,
    name: String,
    id: String,
    feeType: String,
    concessionType: String,
    amount: String,
    statusText: String,
    statusColor: Color,
    statusBgColor: Color,
    statusDotColor: Color
) {
    Row(
        modifier = Modifier
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .clickable { /* TODO */ },
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Student Col (240dp)
        Row(
            modifier = Modifier.width(240.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(Color(0xFFDBEAFE), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(initials, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = Color(0xFF1D4ED8))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(name, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium), color = MaterialTheme.colorScheme.onSurface)
                Text(id, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        
        // Fee Type (200dp)
        Box(modifier = Modifier.width(200.dp)) {
            Text(feeType, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
        }
        
        // Concession Type (180dp)
        Box(modifier = Modifier.width(180.dp)) {
            Box(
                modifier = Modifier
                    .background(Color(0xFFE6E8EA), RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(concessionType, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface)
            }
        }
        
        // Amount (100dp)
        Box(modifier = Modifier.width(100.dp), contentAlignment = Alignment.CenterEnd) {
            Text(amount, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium), color = MaterialTheme.colorScheme.onSurface)
        }
        
        // Status (120dp)
        Box(modifier = Modifier.width(120.dp).padding(start = 16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(statusBgColor, CircleShape)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Box(modifier = Modifier.size(6.dp).background(statusDotColor, CircleShape))
                Spacer(modifier = Modifier.width(6.dp))
                Text(statusText, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium), color = statusColor)
            }
        }
        
        // Actions (120dp)
        Row(
            modifier = Modifier.width(120.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Visibility, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp).padding(end = 8.dp))
            if (statusText == "Pending") {
                Icon(Icons.Default.Edit, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp).padding(end = 8.dp))
                Icon(Icons.Default.Block, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(18.dp))
            } else if (statusText == "Approved") {
                Icon(Icons.Default.Undo, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GrantConcessionModal(onDismiss: () -> Unit) {
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
                Text("Grant New Concession", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface)
                IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            HorizontalDivider(color = Color(0xFFF1F5F9))
            
            // Body (Scrollable)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Student Search
                Column {
                    Text("Student Search", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = "",
                        onValueChange = {},
                        placeholder = { Text("Search by name or ID...", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color(0xFFF1F5F9),
                            focusedBorderColor = Color(0xFF1D4ED8)
                        )
                    )
                }
                
                // Row for Fee Assignment and Type
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Fee Assignment", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurface)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = "Tuition Fee - Fall 2024",
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Color(0xFFF1F5F9))
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Concession Type", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurface)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = "Financial Aid",
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Color(0xFFF1F5F9))
                        )
                    }
                }
                
                // Row for Amount & Percentage
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Concession Value (%)", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurface)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = "25",
                            onValueChange = {},
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Color(0xFFF1F5F9))
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Calculated Amount", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurface)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = "$875.00",
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = Color(0xFFF1F5F9),
                                unfocusedContainerColor = Color(0xFFF8FAFC)
                            )
                        )
                    }
                }
                
                // Reason
                Column {
                    Text("Reason / Remarks", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = "",
                        onValueChange = {},
                        placeholder = { Text("Provide justification for this concession...", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                        modifier = Modifier.fillMaxWidth().height(100.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Color(0xFFF1F5F9))
                    )
                }
                
                // Upload Area
                Column {
                    Text("Supporting Documents", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .background(Color(0xFFF8FAFC), RoundedCornerShape(8.dp))
                            .border(2.dp, Color(0xFFF1F5F9), RoundedCornerShape(8.dp))
                            .clickable { /* TODO */ },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.CloudUpload, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(32.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Click to upload or drag and drop", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                            Text("PDF, JPG, or PNG (max. 10MB)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
            
            HorizontalDivider(color = Color(0xFFF1F5F9))
            
            // Footer
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF8FAFC))
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
                    Text("Submit for Approval")
                }
            }
        }
    }
}
