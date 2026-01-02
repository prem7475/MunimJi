@file:Suppress("DEPRECATION")
package com.example.munimji.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.munimji.data.Transaction
import com.example.munimji.ui.viewmodel.AppViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(viewModel: AppViewModel) {
    var selectedReport by remember { mutableStateOf("profit_loss") }
    val transactions by viewModel.transactions.collectAsState(initial = emptyList())
    val inventory by viewModel.inventory.collectAsState(initial = emptyList())
    val customers by viewModel.customers.collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Financial Reports") }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // Report Type Selector
            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it },
                modifier = Modifier.padding(16.dp)
            ) {
                OutlinedTextField(
                    value = when(selectedReport) {
                        "profit_loss" -> "Profit & Loss Statement"
                        "balance_sheet" -> "Balance Sheet"
                        "gst_report" -> "GST Report"
                        "customer_outstanding" -> "Customer Outstanding"
                        "inventory_valuation" -> "Inventory Valuation"
                        else -> "Profit & Loss Statement"
                    },
                    onValueChange = {},
                    label = { Text("Select Report") },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    readOnly = true
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    listOf(
                        "profit_loss" to "Profit & Loss Statement",
                        "balance_sheet" to "Balance Sheet",
                        "gst_report" to "GST Report",
                        "customer_outstanding" to "Customer Outstanding",
                        "inventory_valuation" to "Inventory Valuation"
                    ).forEach { (value, label) ->
                        DropdownMenuItem(
                            text = { Text(label) },
                            onClick = {
                                selectedReport = value
                                expanded = false
                            }
                        )
                    }
                }
            }

            // Report Content
            when(selectedReport) {
                "profit_loss" -> ProfitLossReport(transactions)
                "balance_sheet" -> BalanceSheetReport(transactions, inventory)
                "gst_report" -> GSTReport(transactions)
                "customer_outstanding" -> CustomerOutstandingReport(customers)
                "inventory_valuation" -> InventoryValuationReport(inventory)
            }
        }
    }
}

@Composable
fun ProfitLossReport(transactions: List<Transaction>) {
    val sales = transactions.filter { it.type == "Sale" }.sumOf { it.totalWithTax }
    val purchases = transactions.filter { it.type == "Purchase" }.sumOf { it.totalWithTax }
    val expenses = transactions.filter { it.type == "Expense" }.sumOf { it.totalWithTax }
    val profit = sales - purchases - expenses

    Card(modifier = Modifier.padding(16.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Profit & Loss Statement", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(16.dp))

            ReportRow("Total Sales", sales)
            ReportRow("Total Purchases", purchases)
            ReportRow("Total Expenses", expenses)
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            ReportRow("Net Profit/Loss", profit, isTotal = true)
        }
    }
}

@Composable
fun BalanceSheetReport(transactions: List<Transaction>, inventory: List<com.example.munimji.data.InventoryItem>) {
    val totalAssets = inventory.sumOf { it.quantity * it.sellPrice }
    val totalLiabilities = transactions.filter { it.isCredit }.sumOf { it.totalWithTax }
    val netWorth = totalAssets - totalLiabilities

    Card(modifier = Modifier.padding(16.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Balance Sheet", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(16.dp))

            Text("Assets", style = MaterialTheme.typography.titleMedium)
            ReportRow("Inventory Value", totalAssets)
            ReportRow("Cash/Bank", 0.0) // TODO: Calculate from wallet and bank accounts

            Spacer(modifier = Modifier.height(16.dp))
            Text("Liabilities", style = MaterialTheme.typography.titleMedium)
            ReportRow("Outstanding Payments", totalLiabilities)

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            ReportRow("Net Worth", netWorth, isTotal = true)
        }
    }
}

@Composable
fun GSTReport(transactions: List<Transaction>) {
    val gstCollected = transactions.filter { it.type == "Sale" }.sumOf { it.gstAmount }
    val gstPaid = transactions.filter { it.type == "Purchase" }.sumOf { it.gstAmount }
    val gstPayable = gstCollected - gstPaid

    Card(modifier = Modifier.padding(16.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("GST Report", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(16.dp))

            ReportRow("GST Collected on Sales", gstCollected)
            ReportRow("GST Paid on Purchases", gstPaid)
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            ReportRow("GST Payable", gstPayable, isTotal = true)
        }
    }
}

@Composable
fun CustomerOutstandingReport(customers: List<com.example.munimji.data.Customer>) {
    LazyColumn(modifier = Modifier.padding(16.dp)) {
        item {
            Text("Customer Outstanding Amounts", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(16.dp))
        }
        items(customers) { customer ->
            Card(modifier = Modifier.padding(vertical = 4.dp)) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(customer.name)
                    Text("₹${customer.totalDue}", style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}

@Composable
fun InventoryValuationReport(inventory: List<com.example.munimji.data.InventoryItem>) {
    val totalValue = inventory.sumOf { it.quantity * it.sellPrice }
    val totalCost = inventory.sumOf { it.quantity * it.buyPrice }
    val potentialProfit = totalValue - totalCost

    Card(modifier = Modifier.padding(16.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Inventory Valuation", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(16.dp))

            ReportRow("Total Inventory Value (Selling)", totalValue)
            ReportRow("Total Cost Value", totalCost)
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            ReportRow("Potential Profit", potentialProfit, isTotal = true)
        }
    }
}

@Composable
fun ReportRow(label: String, amount: Double, isTotal: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            style = if (isTotal) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyLarge
        )
        Text(
            "₹${amount}",
            style = if (isTotal) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyLarge
        )
    }
}
