package com.example.munimji.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.munimji.ui.theme.*
import com.example.munimji.ui.viewmodel.AppViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedDashboardScreen(
    viewModel: AppViewModel,
    onNavigateTo: (String) -> Unit = {}
) {
    val wallet by viewModel.wallet.collectAsState(initial = null)
    val allCheques by viewModel.allCheques.collectAsState(initial = emptyList())
    val allBills by viewModel.allBills.collectAsState(initial = emptyList())

    val scrollState = rememberScrollState()

    // Calculate metrics
    val cashInHand = wallet?.amount ?: 0.0
    val tomorrowCheques = remember(allCheques) {
        allCheques.filter { it.status == "Pending" }.sumOf { it.amount }
    }
    val isAtRisk = tomorrowCheques > cashInHand
    val totalSales = allBills.filter { it.type == "SALE" }.sumOf { it.totalAmount }
    val totalPurchases = allBills.filter { it.type == "PURCHASE" }.sumOf { it.totalAmount }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Text("Business Mate", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, color = NavyPrimary)
        Text("Smart ledger for modern shopkeepers", style = MaterialTheme.typography.labelMedium)

        // KPI Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            KpiMiniCard("Cash", "₹${String.format("%.0f", cashInHand)}", AccountBalanceWallet, modifier = Modifier.weight(1f))
            KpiMiniCard("Sales", "₹${String.format("%.0f", totalSales)}", SuccessGreen, modifier = Modifier.weight(1f))
        }

        // Risk Meter
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = if (isAtRisk) ErrorRed.copy(alpha = 0.1f) else SuccessGreen.copy(alpha = 0.1f)
            ),
            border = BorderStroke(2.dp, if (isAtRisk) ErrorRed else SuccessGreen)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Risk Meter", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (isAtRisk) "⚠️ HIGH RISK" else "✅ SAFE", fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Cheques due tomorrow: ₹${String.format("%.2f", tomorrowCheques)}",
                    color = if (isAtRisk) ErrorRed else SuccessGreen
                )
                if (isAtRisk) {
                    Text("You may face a liquidity issue!", color = ErrorRed, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Quick Action Buttons
        Text("Quick Actions", fontWeight = FontWeight.Bold)
        QuickActionGrid(viewModel, onNavigateTo)

        // Recent Cheques
        if (allCheques.isNotEmpty()) {
            Text("Pending Cheques (${allCheques.filter { it.status == "Pending" }.size})", fontWeight = FontWeight.Bold)
            LazyColumn(modifier = Modifier.heightIn(max = 200.dp)) {
                items(allCheques.take(5)) { cheque ->
                    ChequeAlertCard(cheque)
                }
            }
        }

        // Recent Bills
        if (allBills.isNotEmpty()) {
            Text("Recent Bills", fontWeight = FontWeight.Bold)
            LazyColumn(modifier = Modifier.heightIn(max = 150.dp)) {
                items(allBills.take(3)) { bill ->
                    BillSummaryCard(bill)
                }
            }
        }
    }
}

@Composable
private fun KpiMiniCard(
    title: String,
    value: String,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
            Text(value, style = MaterialTheme.typography.headlineSmall, color = color)
        }
    }
}

@Composable
private fun QuickActionGrid(viewModel: AppViewModel, onNavigateTo: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Row 1
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ElevatedButton(
                onClick = { onNavigateTo("sales_purchase") },
                modifier = Modifier
                    .weight(1f)
                    .height(80.dp),
                colors = ButtonDefaults.elevatedButtonColors(containerColor = SuccessGreen.copy(alpha = 0.1f))
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Filled.ShoppingCart,
                        "Sales",
                        modifier = Modifier.size(24.dp),
                        tint = SuccessGreen
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Sales", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                }
            }
            ElevatedButton(
                onClick = { onNavigateTo("cheques") },
                modifier = Modifier
                    .weight(1f)
                    .height(80.dp),
                colors = ButtonDefaults.elevatedButtonColors(containerColor = NavyPrimary.copy(alpha = 0.1f))
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Filled.Favorite,
                        "Cheques",
                        modifier = Modifier.size(24.dp),
                        tint = NavyPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Cheques", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Row 2
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ElevatedButton(
                onClick = { onNavigateTo("inventory") },
                modifier = Modifier
                    .weight(1f)
                    .height(80.dp),
                colors = ButtonDefaults.elevatedButtonColors(containerColor = WarningAmber.copy(alpha = 0.1f))
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Filled.Edit,
                        "Inventory",
                        modifier = Modifier.size(24.dp),
                        tint = WarningAmber
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Inventory", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                }
            }
            ElevatedButton(
                onClick = { onNavigateTo("reports") },
                modifier = Modifier
                    .weight(1f)
                    .height(80.dp),
                colors = ButtonDefaults.elevatedButtonColors(containerColor = GoldAccent.copy(alpha = 0.1f))
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Filled.Settings,
                        "Reports",
                        modifier = Modifier.size(24.dp),
                        tint = GoldAccent
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Reports", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun ChequeAlertCard(cheque: com.example.munimji.data.Cheque) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = ErrorRed.copy(alpha = 0.05f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Cheque #${cheque.number}", fontWeight = FontWeight.Bold)
                Text("₹${cheque.amount}", style = MaterialTheme.typography.labelSmall)
            }
            Text("📅", style = MaterialTheme.typography.headlineSmall)
        }
    }
}

@Composable
private fun BillSummaryCard(bill: com.example.munimji.data.Bill) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (bill.type == "SALE") SuccessGreen.copy(alpha = 0.05f) else ErrorRed.copy(alpha = 0.05f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Bill #${bill.billNumber}", fontWeight = FontWeight.Bold)
                Text(bill.type, style = MaterialTheme.typography.labelSmall)
            }
            Text("₹${String.format("%.0f", bill.totalAmount)}", fontWeight = FontWeight.Bold)
        }
    }
}
