package com.example.munimji.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.munimji.ui.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillingScreen(viewModel: AppViewModel) {
    var amount by remember { mutableStateOf("") }
    var gstRate by remember { mutableStateOf("") }
    var discount by remember { mutableStateOf("") }
    var paymentMethod by remember { mutableStateOf("Cash") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(value = amount, onValueChange = { amount = it }, label = { Text("Total Amount (Before Tax)") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = gstRate, onValueChange = { gstRate = it }, label = { Text("GST Rate (%)") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = discount, onValueChange = { discount = it }, label = { Text("Discount") }, modifier = Modifier.fillMaxWidth())

        Button(onClick = { /* Create invoice logic */ }, modifier = Modifier.fillMaxWidth()) {
            Text("Create Invoice")
        }
    }
}
