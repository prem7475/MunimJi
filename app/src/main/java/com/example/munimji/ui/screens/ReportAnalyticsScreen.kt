package com.example.munimji.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.munimji.data.Bill
import com.example.munimji.ui.theme.*
import com.example.munimji.ui.viewmodel.AppViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.absoluteValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportAnalyticsScreen(viewModel: AppViewModel) {
    var reportType by remember { mutableStateOf(0) }
    val tabs = listOf("Summary", "Sales", "Purchases", "Ledger")

    val allBills by viewModel.allBills.collectAsState(initial = emptyList())
    val allTransactions by viewModel.transactions.collectAsState(initial = emptyList())
    val allCustomers by viewModel.customers.collectAsState(initial = emptyList())

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(
            selectedTabIndex = reportType,
            containerColor = NavyPrimary,
            contentColor = PearlWhite
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = reportType == index,
                    onClick = { reportType = index },
                    text = { Text(title, fontWeight = FontWeight.Bold) }
                )
            }
        }

        when (reportType) {
            0 -> SummaryReport(allBills, allCustomers)
            1 -> SalesReport(allBills)
            2 -> PurchasesReport(allBills)
            3 -> CustomerLedgerReport(allCustomers, allTransactions)
        }
    }
}

@Composable
private fun SummaryReport(bills: List<Bill>, customers: List<Any>) {
    val totalSales = bills.filter { it.type == "SALE" }.sumOf { it.totalAmount }
    val totalPurchases = bills.filter { it.type == "PURCHASE" }.sumOf { it.totalAmount }
    val profit = totalSales - totalPurchases

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Business Summary", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

        // KPI Cards
        KpiCard("Total Sales", "₹${String.format("%.2f", totalSales)}", SuccessGreen)
        KpiCard("Total Purchases", "₹${String.format("%.2f", totalPurchases)}", ErrorRed)
        KpiCard("Profit/Loss", "₹${String.format("%.2f", profit)}", if (profit >= 0) SuccessGreen else ErrorRed)

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = PearlWhite)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Bill Statistics", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Total Bills: ${bills.size}")
                    Text("Sales: ${bills.filter { it.type == "SALE" }.size}")
                    Text("Purchases: ${bills.filter { it.type == "PURCHASE" }.size}")
                }
            }
        }
    }
}

@Composable
private fun SalesReport(bills: List<Bill>) {
    val salesBills = bills.filter { it.type == "SALE" }
    val totalSales = salesBills.sumOf { it.totalAmount }
    val avgSale = if (salesBills.isNotEmpty()) totalSales / salesBills.size else 0.0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Sales Report", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

        KpiCard("Total Sales", "₹${String.format("%.2f", totalSales)}", SuccessGreen)
        KpiCard("Avg Sale", "₹${String.format("%.2f", avgSale)}", SuccessGreen)
        KpiCard("Sales Count", "${salesBills.size}", SuccessGreen)

        Text("Recent Sales", fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 16.dp))
        salesBills.take(10).forEach { bill ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                colors = CardDefaults.cardColors(containerColor = SuccessGreen.copy(alpha = 0.1f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Bill #${bill.billNumber}", fontWeight = FontWeight.Bold)
                        Text(bill.paymentMode, style = MaterialTheme.typography.labelSmall)
                    }
                    Text("₹${String.format("%.2f", bill.totalAmount)}", fontWeight = FontWeight.Bold, color = SuccessGreen)
                }
            }
        }
    }
}

@Composable
private fun PurchasesReport(bills: List<Bill>) {
    val purchaseBills = bills.filter { it.type == "PURCHASE" }
    val totalPurchases = purchaseBills.sumOf { it.totalAmount }
    val avgPurchase = if (purchaseBills.isNotEmpty()) totalPurchases / purchaseBills.size else 0.0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Purchases Report", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

        KpiCard("Total Purchases", "₹${String.format("%.2f", totalPurchases)}", ErrorRed)
        KpiCard("Avg Purchase", "₹${String.format("%.2f", avgPurchase)}", ErrorRed)
        KpiCard("Purchase Count", "${purchaseBills.size}", ErrorRed)

        Text("Recent Purchases", fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 16.dp))
        purchaseBills.take(10).forEach { bill ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                colors = CardDefaults.cardColors(containerColor = ErrorRed.copy(alpha = 0.1f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Bill #${bill.billNumber}", fontWeight = FontWeight.Bold)
                        Text(bill.vendorName, style = MaterialTheme.typography.labelSmall)
                    }
                    Text("₹${String.format("%.2f", bill.totalAmount)}", fontWeight = FontWeight.Bold, color = ErrorRed)
                }
            }
        }
    }
}

@Composable
private fun CustomerLedgerReport(customers: List<Any>, transactions: List<Any>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Customer Ledger", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text("Track credit (Udhari) given to customers", style = MaterialTheme.typography.labelMedium)

        if (customers.isEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = PearlWhite)
            ) {
                Text(
                    "No customers with credit yet",
                    modifier = Modifier.padding(20.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun KpiCard(title: String, value: String, color: androidx.compose.ui.graphics.Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.15f)),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, style = MaterialTheme.typography.labelMedium)
            Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = color)
        }
    }
}
