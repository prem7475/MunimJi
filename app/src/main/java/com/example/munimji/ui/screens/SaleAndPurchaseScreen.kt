@file:Suppress("DEPRECATION")
package com.example.munimji.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.munimji.data.*
import com.example.munimji.ui.theme.*
import com.example.munimji.ui.viewmodel.AppViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaleAndPurchaseScreen(viewModel: AppViewModel) {
    var activeTab by remember { mutableStateOf(0) }
    val tabs = listOf("Sales", "Purchases", "History")

    Column(modifier = Modifier.fillMaxSize()) {
        // Tab Row
        TabRow(
            selectedTabIndex = activeTab,
            containerColor = NavyPrimary,
            contentColor = PearlWhite
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = activeTab == index,
                    onClick = { activeTab = index },
                    text = { Text(title, fontWeight = FontWeight.Bold) }
                )
            }
        }

        when (activeTab) {
            0 -> SaleEntryForm(viewModel)
            1 -> PurchaseEntryForm(viewModel)
            2 -> BillHistory(viewModel)
        }
    }
}

@Composable
private fun SaleEntryForm(viewModel: AppViewModel) {
    var customerName by remember { mutableStateOf("") }
    var billNumber by remember { mutableStateOf("") }
    var totalAmount by remember { mutableStateOf("") }
    var taxPercent by remember { mutableStateOf("18") }
    var paymentMode by remember { mutableStateOf("CASH") }
    var items by remember { mutableStateOf(listOf<BillItem>()) }
    var showItemPicker by remember { mutableStateOf(false) }

    val allInventory by viewModel.inventory.collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("New Sale", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

        // Bill Number
        OutlinedTextField(
            value = billNumber,
            onValueChange = { billNumber = it },
            label = { Text("Bill Number") },
            modifier = Modifier.fillMaxWidth()
        )

        // Customer Name
        OutlinedTextField(
            value = customerName,
            onValueChange = { customerName = it },
            label = { Text("Customer Name (Party Name)") },
            modifier = Modifier.fillMaxWidth()
        )

        // Payment Mode
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("CASH", "CREDIT").forEach { mode ->
                FilterChip(
                    selected = paymentMode == mode,
                    onClick = { paymentMode = mode },
                    label = { Text(mode) }
                )
            }
        }

        // Items List
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = PearlWhite),
            border = CardDefaults.outlinedCardBorder()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("Items (${items.size})", fontWeight = FontWeight.Bold)
                if (items.isEmpty()) {
                    Text("No items added yet", modifier = Modifier.padding(8.dp), color = Charcoal)
                } else {
                    items.forEachIndexed { index, item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.itemName, fontWeight = FontWeight.Bold)
                                Text("${item.quantity} × ₹${String.format("%.2f", item.unitPrice)} = ₹${String.format("%.2f", item.itemTotal)}")
                            }
                            IconButton(onClick = { items = items.filterIndexed { i, _ -> i != index } }) {
                                Icon(Icons.Filled.Delete, "Delete", tint = ErrorRed)
                            }
                        }
                    }
                }
            }
        }

        // Add Item Button
        Button(
            onClick = { showItemPicker = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
        ) {
            Icon(Icons.Filled.Add, "Add")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Add Item from Inventory")
        }

        // Tax
        OutlinedTextField(
            value = taxPercent,
            onValueChange = { taxPercent = it },
            label = { Text("GST Rate (%)") },
            modifier = Modifier.fillMaxWidth()
        )

        // Total
        OutlinedTextField(
            value = totalAmount,
            onValueChange = { totalAmount = it },
            label = { Text("Total Amount (₹)") },
            modifier = Modifier.fillMaxWidth()
        )

        // Save Button
        Button(
            onClick = {
                if (billNumber.isNotEmpty() && totalAmount.isNotEmpty() && customerName.isNotEmpty()) {
                    val tax = taxPercent.toDoubleOrNull() ?: 18.0
                    val total = totalAmount.toDoubleOrNull() ?: 0.0
                    val taxAmount = total * (tax / 100)
                    
                    val bill = Bill(
                        billNumber = billNumber,
                        type = "SALE",
                        customerId = 0L,
                        vendorName = customerName,
                        totalAmount = total,
                        taxPercentage = tax,
                        taxAmount = taxAmount,
                        paymentMode = paymentMode,
                        status = "COMPLETED"
                    )
                    viewModel.insertBill(bill)
                    
                    // Also insert bill items
                    scope.launch {
                        val lastBillId = 1L // In production, query the last bill ID
                        items.forEach { item ->
                            viewModel.insertBillItem(item.copy(billId = lastBillId))
                        }
                    }
                    
                    billNumber = ""
                    customerName = ""
                    totalAmount = ""
                    items = emptyList()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
        ) {
            Text("Save Sale Bill", color = PearlWhite, fontWeight = FontWeight.Bold)
        }
    }

    // Item Picker Dialog
    if (showItemPicker) {
        AlertDialog(
            onDismissRequest = { showItemPicker = false },
            title = { Text("Select Item from Inventory") },
            text = {
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(allInventory) { inventoryItem ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp),
                            colors = CardDefaults.cardColors(containerColor = PearlWhite)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(inventoryItem.name, fontWeight = FontWeight.Bold)
                                    Text("₹${String.format("%.2f", inventoryItem.sellPrice)}", fontSize = MaterialTheme.typography.bodySmall.fontSize)
                                }
                                Button(
                                    onClick = {
                                        val newItem = BillItem(
                                            itemName = inventoryItem.name,
                                            barcode = inventoryItem.barcode,
                                            quantity = 1.0,
                                            unitPrice = inventoryItem.sellPrice,
                                            itemTotal = inventoryItem.sellPrice
                                        )
                                        items = items + newItem
                                        showItemPicker = false
                                    },
                                    modifier = Modifier.padding(4.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
                                ) {
                                    Text("Add")
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showItemPicker = false }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
private fun PurchaseEntryForm(viewModel: AppViewModel) {
    var vendorName by remember { mutableStateOf("") }
    var billNumber by remember { mutableStateOf("") }
    var totalAmount by remember { mutableStateOf("") }
    var taxPercent by remember { mutableStateOf("18") }
    var paymentMode by remember { mutableStateOf("CASH") }
    var items by remember { mutableStateOf(listOf<BillItem>()) }
    var showItemPicker by remember { mutableStateOf(false) }

    val allInventory by viewModel.inventory.collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("New Purchase", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

        // Bill Number
        OutlinedTextField(
            value = billNumber,
            onValueChange = { billNumber = it },
            label = { Text("Bill Number") },
            modifier = Modifier.fillMaxWidth()
        )

        // Vendor Name
        OutlinedTextField(
            value = vendorName,
            onValueChange = { vendorName = it },
            label = { Text("Vendor Name / Supplier") },
            modifier = Modifier.fillMaxWidth()
        )

        // Payment Mode
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("CASH", "CREDIT").forEach { mode ->
                FilterChip(
                    selected = paymentMode == mode,
                    onClick = { paymentMode = mode },
                    label = { Text(mode) }
                )
            }
        }

        // Items List
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = PearlWhite),
            border = CardDefaults.outlinedCardBorder()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("Items (${items.size})", fontWeight = FontWeight.Bold)
                if (items.isEmpty()) {
                    Text("No items added yet", modifier = Modifier.padding(8.dp), color = Charcoal)
                } else {
                    items.forEachIndexed { index, item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.itemName, fontWeight = FontWeight.Bold)
                                Text("${item.quantity} × ₹${String.format("%.2f", item.unitPrice)} = ₹${String.format("%.2f", item.itemTotal)}")
                            }
                            IconButton(onClick = { items = items.filterIndexed { i, _ -> i != index } }) {
                                Icon(Icons.Filled.Delete, "Delete", tint = ErrorRed)
                            }
                        }
                    }
                }
            }
        }

        // Add Item Button
        Button(
            onClick = { showItemPicker = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
        ) {
            Icon(Icons.Filled.Add, "Add")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Add Item from Inventory")
        }

        // Tax
        OutlinedTextField(
            value = taxPercent,
            onValueChange = { taxPercent = it },
            label = { Text("GST Rate (%)") },
            modifier = Modifier.fillMaxWidth()
        )

        // Total
        OutlinedTextField(
            value = totalAmount,
            onValueChange = { totalAmount = it },
            label = { Text("Total Amount (₹)") },
            modifier = Modifier.fillMaxWidth()
        )

        // Save Button
        Button(
            onClick = {
                if (billNumber.isNotEmpty() && totalAmount.isNotEmpty() && vendorName.isNotEmpty()) {
                    val tax = taxPercent.toDoubleOrNull() ?: 18.0
                    val total = totalAmount.toDoubleOrNull() ?: 0.0
                    val taxAmount = total * (tax / 100)
                    
                    val bill = Bill(
                        billNumber = billNumber,
                        type = "PURCHASE",
                        vendorName = vendorName,
                        totalAmount = total,
                        taxPercentage = tax,
                        taxAmount = taxAmount,
                        paymentMode = paymentMode,
                        status = "COMPLETED"
                    )
                    viewModel.insertBill(bill)
                    
                    scope.launch {
                        val lastBillId = 1L
                        items.forEach { item ->
                            viewModel.insertBillItem(item.copy(billId = lastBillId))
                        }
                    }
                    
                    billNumber = ""
                    vendorName = ""
                    totalAmount = ""
                    items = emptyList()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
        ) {
            Text("Save Purchase Bill", color = PearlWhite, fontWeight = FontWeight.Bold)
        }
    }

    // Item Picker Dialog
    if (showItemPicker) {
        AlertDialog(
            onDismissRequest = { showItemPicker = false },
            title = { Text("Select Item from Inventory") },
            text = {
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(allInventory) { inventoryItem ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp),
                            colors = CardDefaults.cardColors(containerColor = PearlWhite)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(inventoryItem.name, fontWeight = FontWeight.Bold)
                                    Text("₹${String.format("%.2f", inventoryItem.buyPrice)}", fontSize = MaterialTheme.typography.bodySmall.fontSize)
                                }
                                Button(
                                    onClick = {
                                        val newItem = BillItem(
                                            itemName = inventoryItem.name,
                                            barcode = inventoryItem.barcode,
                                            quantity = 1.0,
                                            unitPrice = inventoryItem.buyPrice,
                                            itemTotal = inventoryItem.buyPrice
                                        )
                                        items = items + newItem
                                        showItemPicker = false
                                    },
                                    modifier = Modifier.padding(4.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
                                ) {
                                    Text("Add")
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showItemPicker = false }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
private fun BillHistory(viewModel: AppViewModel) {
    val bills by viewModel.allBills.collectAsState(initial = emptyList())

    LazyColumn(modifier = Modifier.padding(16.dp)) {
        item {
            Text("Recent Bills", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 12.dp))
        }
        items(bills) { bill ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = PearlWhite)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(bill.billNumber, fontWeight = FontWeight.Bold)
                    Text("${bill.type} - ₹${String.format("%.2f", bill.totalAmount)}", color = NavyPrimary)
                    Text(bill.vendorName, fontSize = MaterialTheme.typography.bodySmall.fontSize, color = Charcoal)
                }
            }
        }
    }
}
