package com.example.munimji.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.munimji.data.Cheque
import com.example.munimji.ui.theme.*
import com.example.munimji.ui.viewmodel.AppViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(viewModel: AppViewModel, onNavigateToCheques: () -> Unit) {
    val wallet by viewModel.wallet.collectAsState(initial = null)
    val allCheques by viewModel.allCheques.collectAsState(initial = emptyList())

    val scrollState = rememberScrollState()

    // Calculate risk meter
    val tomorrowCheques = remember(allCheques) {
        val tomorrow = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, 1)
        }.time
        allCheques.filter { cheque ->
            cheque.status == "Pending" && cheque.date == SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(tomorrow)
        }.sumOf { it.amount }
    }

    val cashInHand = wallet?.amount ?: 0.0
    val isAtRisk = tomorrowCheques > cashInHand

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Wallet Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Cash in Hand (Galla)", style = MaterialTheme.typography.titleMedium)
                Text(
                    "₹${String.format("%.2f", cashInHand)}",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Risk Meter
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = if (isAtRisk) ErrorRed.copy(alpha = 0.1f) else SuccessGreen.copy(alpha = 0.1f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            border = BorderStroke(1.dp, if (isAtRisk) ErrorRed else SuccessGreen)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Risk Meter",
                        style = MaterialTheme.typography.titleLarge,
                        color = NavyPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isAtRisk) "⚠️" else "✅",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (isAtRisk) "High Risk: Cheques due tomorrow exceed cash in hand!"
                    else "Safe: Sufficient cash for tomorrow's cheques",
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (isAtRisk) ErrorRed else SuccessGreen,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Tomorrow's Cheques: ₹${String.format("%.2f", tomorrowCheques)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Charcoal
                )
            }
        }

        // Cheque Alerts
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Pending Cheques", style = MaterialTheme.typography.titleMedium)
                    TextButton(onClick = onNavigateToCheques) {
                        Text("View All")
                    }
                }
                if (allCheques.filter { it.status == "Pending" }.isEmpty()) {
                    Text("No pending cheques. Safe!", color = Color.Green)
                } else {
                    allCheques.filter { it.status == "Pending" }.take(3).forEach { cheque ->
                        ChequeAlertItem(cheque = cheque)
                    }
                }
            }
        }
    }
}

@Composable
fun ChequeAlertItem(cheque: Cheque) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Platinum.copy(alpha = 0.3f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = cheque.partyName,
                    style = MaterialTheme.typography.bodyLarge,
                    color = NavyPrimary,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "#${cheque.number} • ${cheque.date}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Charcoal
                )
            }
            Text(
                text = "₹${String.format("%.2f", cheque.amount)}",
                style = MaterialTheme.typography.bodyLarge,
                color = WarningAmber,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
