@file:Suppress("DEPRECATION")
package com.example.munimji.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.munimji.data.AppRepository
import com.example.munimji.data.InventoryItem
import com.example.munimji.ui.theme.NavyPrimary
import com.example.munimji.utils.ExcelManager
import kotlinx.coroutines.launch
import androidx.compose.material3.ExperimentalMaterial3Api

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ExcelImportScreen(
    navController: NavController,
    repository: AppRepository
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var importedItems by remember { mutableStateOf<List<InventoryItem>>(emptyList()) }
    var showSuccess by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var isImporting by remember { mutableStateOf(false) }

    val filePickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            isImporting = true
            scope.launch {
                try {
                    val items = ExcelManager.importItemsFromExcel(context, uri)
                    if (items.isNotEmpty()) {
                        importedItems = items
                    } else {
                        errorMessage = "No items found in CSV file"
                        showError = true
                    }
                } catch (e: Exception) {
                    errorMessage = "Error: ${e.message}"
                    showError = true
                } finally {
                    isImporting = false
                }
            }
        }
    }

    if (showError) {
        AlertDialog(
            onDismissRequest = { showError = false },
            title = { Text("Error") },
            text = { Text(errorMessage) },
            confirmButton = {
                Button(onClick = { showError = false }) { Text("OK") }
            }
        )
    }

    if (showSuccess) {
        AlertDialog(
            onDismissRequest = { showSuccess = false; navController.popBackStack() },
            title = { Text("Success") },
            text = { Text("${importedItems.size} items imported!") },
            confirmButton = {
                Button(onClick = { showSuccess = false; navController.popBackStack() }) {
                    Text("Done")
                }
            }
        )
    }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF5F5F5))) {
        TopAppBar(
            title = { Text("Import Items from Excel", fontWeight = FontWeight.Bold) },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = NavyPrimary,
                titleContentColor = Color.White,
                navigationIconContentColor = Color.White
            )
        )

        if (importedItems.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.height(80.dp))
                Spacer(modifier = Modifier.height(16.dp))
                Text("Select CSV File", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Item Name, Quantity, Buy Price, Sell Price, Barcode", fontSize = 12.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { filePickerLauncher.launch(arrayOf("text/csv", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isImporting
                ) {
                    if (isImporting) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                    } else {
                        // icon removed for compatibility
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Choose File", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                Text("Review (${importedItems.size} items)", fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 12.dp))
                LazyColumn(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    items(importedItems) { item ->
                        Card(modifier = Modifier.fillMaxWidth().padding(4.dp), shape = RoundedCornerShape(8.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(item.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Qty: ${item.quantity} | Buy: ₹${String.format("%.2f", item.buyPrice)} | Sell: ₹${String.format("%.2f", item.sellPrice)}", fontSize = 12.sp, color = Color.Gray)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = { importedItems = emptyList() }, modifier = Modifier.weight(1f).height(50.dp), shape = RoundedCornerShape(12.dp)) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = {
                            scope.launch {
                                try {
                                    importedItems.forEach { repository.insertInventoryItem(it) }
                                    showSuccess = true
                                } catch (e: Exception) {
                                    errorMessage = "Error: ${e.message}"
                                    showError = true
                                }
                            }
                        },
                        modifier = Modifier.weight(1f).height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Save Items", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
