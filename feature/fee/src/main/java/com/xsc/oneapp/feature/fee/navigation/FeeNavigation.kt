package com.xsc.oneapp.feature.fee.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.xsc.oneapp.feature.fee.ui.screen.FeeManagementScreen
import com.xsc.oneapp.feature.fee.ui.screen.InvoiceDetailsScreen
import com.xsc.oneapp.feature.fee.ui.screen.PaymentsLedgerScreen
import com.xsc.oneapp.feature.fee.ui.screen.ConcessionsQueueScreen
import com.xsc.oneapp.feature.fee.ui.screen.FeesRefundsScreen
import com.xsc.oneapp.feature.fee.ui.screen.PenaltiesRulesScreen
import com.xsc.oneapp.feature.fee.ui.screen.ReportsOverviewScreen

fun NavGraphBuilder.feeGraph(
    navController: NavHostController,
    onNavigateToInvoice: (String) -> Unit,
    onNavigateToPaymentsLedger: () -> Unit,
    onNavigateToConcessions: () -> Unit,
    onNavigateToRefunds: () -> Unit,
    onNavigateToPenalties: () -> Unit,
    onNavigateToReports: () -> Unit,
    onBack: () -> Unit
) {
    navigation(startDestination = "fee/management", route = "fees") {
        composable("fee/management") {
            FeeManagementScreen(
                onBack = onBack,
                onNavigateToInvoice = onNavigateToInvoice,
                onNavigateToPaymentsLedger = onNavigateToPaymentsLedger,
                onNavigateToConcessions = onNavigateToConcessions,
                onNavigateToRefunds = onNavigateToRefunds,
                onNavigateToPenalties = onNavigateToPenalties,
                onNavigateToReports = onNavigateToReports
            )
        }
        
        composable("invoice_details/{invoiceId}") { backStackEntry ->
            val invoiceId = backStackEntry.arguments?.getString("invoiceId") ?: return@composable
            InvoiceDetailsScreen(
                invoiceId = invoiceId,
                onBack = onBack
            )
        }
        
        composable("payments_ledger") {
            PaymentsLedgerScreen(
                onBack = onBack
            )
        }

        composable("fee/concessions") {
            ConcessionsQueueScreen(
                onBack = onBack
            )
        }
        
        composable("fee/refunds") {
            FeesRefundsScreen(
                onBack = onBack
            )
        }
        
        composable("fee/penalties") {
            PenaltiesRulesScreen(
                onBack = onBack
            )
        }
        
        composable("fee/reports") {
            ReportsOverviewScreen(
                onBack = onBack
            )
        }
    }
}
