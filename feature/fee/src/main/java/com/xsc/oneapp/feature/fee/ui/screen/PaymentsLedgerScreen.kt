package com.xsc.oneapp.feature.fee.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.xsc.sdk.commonui.record.RecordCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentsLedgerScreen(
    onBack: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Payments Ledger") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO */ }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search receipts, students...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                    focusedBorderColor = MaterialTheme.colorScheme.primary
                )
            )
            
            // Filters
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FilterDropdown("All Methods", Modifier.weight(1f))
                FilterDropdown("All Statuses", Modifier.weight(1f))
                FilterDropdown("Date Range", Modifier.weight(1f))
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Ledger List
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth().weight(1f)
            ) {
                item {
                    LedgerCard(
                        initials = "AS",
                        initialsBgColor = Color(0xFFDBEAFE),
                        initialsColor = Color(0xFF1E3A8A),
                        name = "Aarav Sharma",
                        receiptId = "#REC-8923",
                        feeType = "Tuition Fee",
                        amount = "$3,500.00",
                        statusText = "PAID",
                        statusColor = Color(0xFF10B981),
                        statusBgColor = Color(0xFFD1FAE5),
                        date = "Oct 02",
                        methodIcon = Icons.Default.CreditCard,
                        methodText = "Visa ending in 4242"
                    )
                }
                item {
                    LedgerCard(
                        initials = "PP",
                        initialsBgColor = Color(0xFFFEE2E2),
                        initialsColor = Color(0xFF991B1B),
                        name = "Priya Patel",
                        receiptId = "#REC-8924",
                        feeType = "Library Fine",
                        amount = "$50.00",
                        statusText = "FAILED",
                        statusColor = Color(0xFFEF4444),
                        statusBgColor = Color(0xFFFEE2E2),
                        date = "Oct 01",
                        methodIcon = Icons.Default.Money,
                        methodText = "Bank Transfer"
                    )
                }
                item {
                    LedgerCard(
                        initials = "RD",
                        initialsBgColor = Color(0xFFFEF3C7),
                        initialsColor = Color(0xFF92400E),
                        name = "Rahul Desai",
                        receiptId = "#REC-8925",
                        feeType = "Exam Fee",
                        amount = "$350.00",
                        statusText = "PENDING",
                        statusColor = Color(0xFFF59E0B),
                        statusBgColor = Color(0xFFFEF3C7),
                        date = "Sep 30",
                        methodIcon = Icons.Default.Money,
                        methodText = "Cheque #1024"
                    )
                }
            }
        }
    }
}

@Composable
private fun FilterDropdown(label: String, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        color = MaterialTheme.colorScheme.surface,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .clickable { /* TODO: Show dropdown */ }
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface, maxLines = 1)
            Icon(Icons.Default.ArrowDropDown, contentDescription = null, modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
private fun LedgerCard(
    initials: String,
    initialsBgColor: Color,
    initialsColor: Color,
    name: String,
    receiptId: String,
    feeType: String,
    amount: String,
    statusText: String,
    statusColor: Color,
    statusBgColor: Color,
    date: String,
    methodIcon: ImageVector,
    methodText: String
) {
    RecordCard(modifier = Modifier.fillMaxWidth()) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(initialsBgColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(initials, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = initialsColor)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(name, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium))
                        Text(receiptId, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(feeType, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Text(amount, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = statusText,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = statusColor,
                        modifier = Modifier
                            .background(statusBgColor, RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(date, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(methodIcon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(methodText, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
