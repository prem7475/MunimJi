@file:Suppress("DEPRECATION")
package com.example.munimji.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.munimji.ui.theme.*
import com.example.munimji.utils.ReceiptGenerator
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentReceiptScreen(
    onNavigateBack: () -> Unit = {}
) {
    var paymentMethod by remember { mutableStateOf("Cash") }
    var amount by remember { mutableStateOf("") }
    var customerName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var referenceNumber by remember { mutableStateOf("") }
    var showSuccess by remember { mutableStateOf(false) }
    var generatedReceipt by remember { mutableStateOf("") }
    
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // Header
        TopAppBar(
            title = { Text("Payment Receipt", fontWeight = FontWeight.Bold) },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Filled.ArrowBack, "Back")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = NavyPrimary,
                titleContentColor = Color.White,
                navigationIconContentColor = Color.White
            )
        )
        
        if (showSuccess) {
            // Receipt Display
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Success Message
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(2.dp, RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = SuccessGreen.copy(alpha = 0.1f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = SuccessGreen)
                        Text("Payment receipt generated successfully!", color = SuccessGreen, fontWeight = FontWeight.Bold)
                    }
                }
                
                // Receipt Content
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(2.dp, RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    SelectionContainer {
                        Text(
                            generatedReceipt,
                            modifier = Modifier.padding(16.dp),
                            fontSize = 11.sp,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                            lineHeight = 14.sp
                        )
                    }
                }
                
                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ElevatedButton(
                        onClick = { showSuccess = false },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("New Receipt")
                    }
                    
                    ElevatedButton(
                        onClick = { /* Share receipt */ },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.elevatedButtonColors(containerColor = AccountBalanceWallet)
                    ) {
                        Icon(Icons.Filled.Share, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Share")
                    }
                }
            }
        } else {
            // Form
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        "Create Payment Receipt",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                // Customer Name
                item {
                    OutlinedTextField(
                        value = customerName,
                        onValueChange = { customerName = it },
                        label = { Text("Customer Name") },
                        leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
                
                // Payment Method
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(1.dp, RoundedCornerShape(12.dp)),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Payment Method", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf("Cash", "Check", "Card", "Online").forEach { method ->
                                    FilterChip(
                                        selected = paymentMethod == method,
                                        onClick = { paymentMethod = method },
                                        label = { Text(method, fontSize = 12.sp) },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }
                }
                
                // Amount
                item {
                    OutlinedTextField(
                            value = amount,
                            onValueChange = { amount = it },
                            label = { Text("Amount (₹)") },
                            leadingIcon = { Icon(Icons.Filled.ShoppingCart, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                }
                
                // Reference Number
                item {
                    OutlinedTextField(
                        value = referenceNumber,
                        onValueChange = { referenceNumber = it },
                        label = { Text("Reference Number (Optional)") },
                        leadingIcon = { Icon(Icons.Filled.ShoppingCart, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
                
                // Description
                item {
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description (Optional)") },
                        leadingIcon = { Icon(Icons.Filled.Edit, contentDescription = null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
                
                // Generate Button
                item {
                    ElevatedButton(
                        onClick = {
                            if (customerName.isNotBlank() && amount.isNotBlank()) {
                                val receiptNumber = "RCP${System.currentTimeMillis() % 10000}"
                                generatedReceipt = ReceiptGenerator.generatePaymentReceipt(
                                    receiptNumber = receiptNumber,
                                    paymentDate = System.currentTimeMillis(),
                                    payer = customerName,
                                    amount = amount.toDoubleOrNull() ?: 0.0,
                                    paymentMethod = paymentMethod,
                                    description = description,
                                    referenceNumber = referenceNumber
                                )
                                showSuccess = true
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.elevatedButtonColors(containerColor = SuccessGreen)
                    ) {
                        Icon(Icons.Filled.CheckCircle, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Generate Receipt", fontWeight = FontWeight.Bold)
                    }
                }
                
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun SelectionContainer(content: @Composable () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth()) {
        content()
    }
}
