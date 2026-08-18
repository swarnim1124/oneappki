package com.xsc.oneapp.feature.fee.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.xsc.sdk.commonui.record.RecordScaffold
import com.xsc.sdk.commonui.record.SectionChips
import com.xsc.oneapp.feature.fee.ui.tabs.OverviewTab
import com.xsc.oneapp.feature.fee.ui.tabs.StructuresTab
import com.xsc.oneapp.feature.fee.ui.tabs.AssignmentsTab
import com.xsc.oneapp.feature.fee.ui.tabs.InvoicesTab

private val TAB_TITLES = listOf("Overview", "Structures", "Assignments", "Invoices")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeeManagementScreen(
    onBack: () -> Unit,
    onNavigateToInvoice: (String) -> Unit,
    onNavigateToPaymentsLedger: () -> Unit,
    onNavigateToConcessions: () -> Unit,
    onNavigateToRefunds: () -> Unit,
    onNavigateToPenalties: () -> Unit,
    onNavigateToReports: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    
    val topBarTitle = when (selectedTab) {
        0 -> "Fees Management"
        else -> "OneApp ERP"
    }

    RecordScaffold(
        title = topBarTitle,
        onBack = onBack
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Dynamic Contextual Headers
            when (selectedTab) {
                1 -> {
                    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                        Text("Fee Structures", style = MaterialTheme.typography.headlineMedium)
                        Text("Manage and configure academic fee setups.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { /* TODO: Create structure */ }) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Create Structure")
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
                2 -> {
                    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                        Text("Fee Assignments", style = MaterialTheme.typography.headlineMedium)
                        Text("Manage and track student fee allocations for the current term.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { /* TODO: Assign fee */ }) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Assign Fee")
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
            
            SectionChips(
                options = TAB_TITLES,
                selectedIndex = selectedTab,
                onSelect = { selectedTab = it }
            )
            
            HorizontalDivider(modifier = Modifier.padding(bottom = 16.dp))
            
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                when (selectedTab) {
                    0 -> OverviewTab(
                        onNavigateToPaymentsLedger = onNavigateToPaymentsLedger,
                        onNavigateToConcessions = onNavigateToConcessions,
                        onNavigateToRefunds = onNavigateToRefunds,
                        onNavigateToPenalties = onNavigateToPenalties,
                        onNavigateToReports = onNavigateToReports
                    )
                    1 -> StructuresTab()
                    2 -> AssignmentsTab()
                    3 -> InvoicesTab(
                        onNavigateToInvoice = onNavigateToInvoice
                    )
                }
            }
        }
    }
}
