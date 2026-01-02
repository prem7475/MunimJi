package com.example.munimji.ui.screens

import android.content.Intent
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.munimji.data.InventoryItem
import com.example.munimji.ui.theme.*
import com.example.munimji.ui.viewmodel.AppViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryManagementScreen(viewModel: AppViewModel) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val inventory by viewModel.allInventory.collectAsState(initial = emptyList())

    var showAddDialog by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf<InventoryItem?>(null) }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            scope.launch {
                try {
                    val imported = com.example.munimji.utils.ExcelManager.importItemsFromExcel(context, uri)
                    if (imported.isNotEmpty()) {
                        imported.forEach { item ->
                            viewModel.insertInventoryItem(item)
                        }
                        Toast.makeText(context, "Imported ${imported.size} items", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "No items found in file", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, "Import failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Header
        Text(
            text = "Inventory Management",
            style = MaterialTheme.typography.headlineMedium,
            color = NavyPrimary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Action Buttons
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = { showAddDialog = true },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Item")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add Item")
            }

            OutlinedButton(
                onClick = { filePickerLauncher.launch("*/*") },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = NavyPrimary)
            ) {
                Icon(Icons.Default.Share, contentDescription = "Import Excel")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Import Excel")
            }

            OutlinedButton(
                onClick = {
                    scope.launch {
                        try {
                            val file = com.example.munimji.utils.ExcelManager.exportInventoryToCSV(context, inventory)
                            if (file != null) {
                                val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
                                val intent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/csv"
                                    putExtra(Intent.EXTRA_STREAM, uri)
                                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                }
                                context.startActivity(Intent.createChooser(intent, "Export Inventory"))
                            } else {
                                Toast.makeText(context, "Export failed", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(context, "Export failed: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = NavyPrimary)
            ) {
                Icon(Icons.Default.Share, contentDescription = "Export Excel")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Export Excel")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Inventory List
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(inventory) { item ->
                InventoryItemCard(
                    item = item,
                    onEdit = { selectedItem = item; showAddDialog = true },
                    onDelete = {
                        scope.launch {
                            viewModel.deleteInventoryItem(item)
                        }
                    }
                )
            }
        }
    }

    // Add/Edit Dialog
    if (showAddDialog) {
        InventoryItemDialog(
            item = selectedItem,
            onDismiss = { showAddDialog = false; selectedItem = null },
            onSave = { item ->
                scope.launch {
                    if (selectedItem != null) {
                        viewModel.updateInventoryItem(item)
                    } else {
                        viewModel.insertInventoryItem(item)
                    }
                    showAddDialog = false
                    selectedItem = null
                }
            }
        )
    }
}

@Composable
fun InventoryItemCard(
    item: InventoryItem,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = PearlWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = NavyPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Barcode: ${item.barcode}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Charcoal
                    )
                }
                Text(
                    text = "Qty: ${item.quantity}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (item.quantity > 0) SuccessGreen else ErrorRed,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Buy: ₹${String.format("%.2f", item.buyPrice)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Charcoal
                )
                Text(
                    text = "Sell: ₹${String.format("%.2f", item.sellPrice)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = NavyPrimary,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onEdit) {
                    Text("Edit", color = NavyPrimary)
                }
                TextButton(onClick = onDelete) {
                    Text("Delete", color = ErrorRed)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryItemDialog(
    item: InventoryItem?,
    onDismiss: () -> Unit,
    onSave: (InventoryItem) -> Unit
) {
    var name by remember { mutableStateOf(item?.name ?: "") }
    var buyPrice by remember { mutableStateOf(item?.buyPrice?.toString() ?: "") }
    var sellPrice by remember { mutableStateOf(item?.sellPrice?.toString() ?: "") }
    var quantity by remember { mutableStateOf(item?.quantity?.toString() ?: "") }
    var barcode by remember { mutableStateOf(item?.barcode ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (item == null) "Add Item" else "Edit Item",
                color = NavyPrimary,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Item Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = buyPrice,
                    onValueChange = { buyPrice = it },
                    label = { Text("Buy Price") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = sellPrice,
                    onValueChange = { sellPrice = it },
                    label = { Text("Sell Price") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    label = { Text("Quantity") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = barcode,
                    onValueChange = { barcode = it },
                    label = { Text("Barcode (Optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val buy = buyPrice.toDoubleOrNull() ?: 0.0
                    val sell = sellPrice.toDoubleOrNull() ?: 0.0
                    val qty = quantity.toIntOrNull() ?: 0

                    val newItem = InventoryItem(
                        id = item?.id ?: 0,
                        name = name,
                        buyPrice = buy,
                        sellPrice = sell,
                        quantity = qty,
                        barcode = barcode
                    )
                    onSave(newItem)
                },
                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
