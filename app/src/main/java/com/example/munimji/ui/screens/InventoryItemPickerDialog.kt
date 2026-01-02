@file:Suppress("DEPRECATION")
package com.example.munimji.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.munimji.data.InventoryItem
import com.example.munimji.ui.theme.*

@Composable
fun InventoryItemPickerDialog(
    items: List<InventoryItem>,
    onItemSelected: (InventoryItem, Int) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedItem by remember { mutableStateOf<InventoryItem?>(null) }
    var quantity by remember { mutableStateOf("1") }
    var searchQuery by remember { mutableStateOf("") }

    val filteredItems = items.filter {
        it.name.contains(searchQuery, ignoreCase = true)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Inventory Item") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Search field
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Search by name or barcode") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    leadingIcon = { Icon(Icons.Filled.Search, "Search") },
                    singleLine = true
                )

                if (selectedItem == null) {
                    // Item list
                    LazyColumn(modifier = Modifier.heightIn(max = 250.dp)) {
                        items(filteredItems) { item ->
                            InventoryItemCard(
                                item = item,
                                onSelect = { selectedItem = it }
                            )
                        }
                    }
                } else {
                    // Quantity selector
                    Column {
                        Text(
                            "Selected: ${selectedItem?.name}",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            "Price: ₹${String.format("%.2f", selectedItem?.sellPrice)}",
                            style = MaterialTheme.typography.labelSmall
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Quantity:", modifier = Modifier.weight(1f))
                            IconButton(onClick = {
                                val q = (quantity.toIntOrNull() ?: 1) - 1
                                if (q > 0) quantity = q.toString()
                            }) {
                                Icon(Icons.Filled.Close, "Decrease")
                            }
                            TextField(
                                value = quantity,
                                onValueChange = { if (it.all { c -> c.isDigit() }) quantity = it },
                                modifier = Modifier
                                    .width(60.dp)
                                    .height(40.dp),
                                textStyle = MaterialTheme.typography.labelMedium
                            )
                            IconButton(onClick = {
                                val q = (quantity.toIntOrNull() ?: 0) + 1
                                quantity = q.toString()
                            }) {
                                Icon(Icons.Filled.Add, "Increase")
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                selectedItem?.let { item ->
                                    onItemSelected(item, quantity.toIntOrNull() ?: 1)
                                    onDismiss()
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Add to Bill")
                        }

                        TextButton(
                            onClick = { selectedItem = null },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Choose Different Item")
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun InventoryItemCard(
    item: InventoryItem,
    onSelect: (InventoryItem) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onSelect(item) },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(item.name, fontWeight = FontWeight.Bold)
                Text(
                    "Price: ₹${String.format("%.2f", item.sellPrice)}",
                    style = MaterialTheme.typography.labelSmall
                )
                Text(
                    "Stock: ${item.quantity}",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (item.quantity > 10) SuccessGreen else WarningAmber
                )
            }
            Icon(Icons.Filled.ArrowForward, "Select", tint = NavyPrimary)
        }
    }
}
