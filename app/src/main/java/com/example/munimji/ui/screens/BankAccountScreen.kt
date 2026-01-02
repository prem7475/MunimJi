package com.example.munimji.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.munimji.data.BankAccount
import com.example.munimji.ui.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BankAccountScreen(viewModel: AppViewModel) {
    val bankAccounts by viewModel.bankAccounts.collectAsState(initial = emptyList())
    var showForm by remember { mutableStateOf(false) }
    var formData by remember { mutableStateOf(BankAccount(name = "", accountNumber = "", bankName = "")) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bank Accounts") },
                actions = {
                    Button(onClick = { showForm = true }) {
                        Text("Add Account")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            if (showForm) {
                BankAccountForm(
                    account = formData,
                    onSave = {
                        viewModel.insertBankAccount(it)
                        showForm = false
                        formData = BankAccount(name = "", accountNumber = "", bankName = "")
                    },
                    onCancel = {
                        showForm = false
                        formData = BankAccount(name = "", accountNumber = "", bankName = "")
                    },
                    onChange = { formData = it }
                )
            }

            LazyColumn {
                items(bankAccounts) { account ->
                    BankAccountItem(account = account)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BankAccountForm(
    account: BankAccount,
    onSave: (BankAccount) -> Unit,
    onCancel: () -> Unit,
    onChange: (BankAccount) -> Unit
) {
    var name by remember { mutableStateOf(account.name) }
    var accountNumber by remember { mutableStateOf(account.accountNumber) }
    var bankName by remember { mutableStateOf(account.bankName) }
    var ifscCode by remember { mutableStateOf(account.ifscCode) }
    var balance by remember { mutableStateOf(account.balance.toString()) }

    Card(modifier = Modifier.padding(16.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Add Bank Account", style = MaterialTheme.typography.headlineSmall)

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Account Name") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = accountNumber,
                onValueChange = { accountNumber = it },
                label = { Text("Account Number") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = bankName,
                onValueChange = { bankName = it },
                label = { Text("Bank Name") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = ifscCode,
                onValueChange = { ifscCode = it },
                label = { Text("IFSC Code") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = balance,
                onValueChange = { balance = it },
                label = { Text("Opening Balance") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row {
                Button(
                    onClick = onCancel,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancel")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        val bal = balance.toDoubleOrNull() ?: 0.0
                        onSave(account.copy(
                            name = name,
                            accountNumber = accountNumber,
                            bankName = bankName,
                            ifscCode = ifscCode,
                            balance = bal
                        ))
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Save")
                }
            }
        }
    }
}

@Composable
fun BankAccountItem(account: BankAccount) {
    Card(modifier = Modifier.padding(8.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(account.name, style = MaterialTheme.typography.headlineSmall)
            Text("Account: ${account.accountNumber}")
            Text("Bank: ${account.bankName}")
            Text("Balance: ₹${account.balance}", style = MaterialTheme.typography.bodyLarge)
        }
    }
}
