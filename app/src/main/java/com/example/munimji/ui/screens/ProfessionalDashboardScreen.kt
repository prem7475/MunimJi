package com.example.munimji.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.munimji.ui.theme.*
import com.example.munimji.ui.viewmodel.AppViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfessionalDashboardScreen(
    viewModel: AppViewModel,
    onNavigateTo: (String) -> Unit = {}
) {
    val wallet by viewModel.wallet.collectAsState(initial = null)
    val allCheques by viewModel.allCheques.collectAsState(initial = emptyList())
    val allBills by viewModel.allBills.collectAsState(initial = emptyList())
    val allInventory by viewModel.allInventory.collectAsState(initial = emptyList())

    val scrollState = rememberScrollState()

    // Calculate metrics
    val cashInHand = wallet?.amount ?: 0.0
    val pendingCheques = allCheques.filter { it.status == "Pending" }.sumOf { it.amount }
    val isAtRisk = pendingCheques > cashInHand
    val riskColor by animateColorAsState(
        targetValue = if (isAtRisk) ErrorRed else SuccessGreen,
        label = "riskColor"
    )
    
    val salesBills = allBills.filter { it.type == "SALE" }
    val purchaseBills = allBills.filter { it.type == "PURCHASE" }
    val totalSales = salesBills.sumOf { it.totalAmount }
    val totalPurchases = purchaseBills.sumOf { it.totalAmount }
    val profit = totalSales - totalPurchases
    val lowStockItems = allInventory.filter { it.quantity < 10 }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Premium Header with Gradient
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
                    .background(NavyPrimary)
                    .padding(24.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Business Mate",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Welcome back!",
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                        Icon(
                            Icons.Filled.Info,
                            contentDescription = "Welcome",
                            tint = GoldAccent,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // KPI Cards Section
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    "Financial Overview",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 4.dp)
                )

                // 2x2 KPI Grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ProfessionalKPICard(
                        label = "Cash In Hand",
                        value = "₹${String.format("%.0f", cashInHand)}",
                        icon = Icons.Filled.ShoppingCart,
                        color = AccountBalanceWallet,
                        modifier = Modifier.weight(1f)
                    )
                    ProfessionalKPICard(
                        label = "Total Sales",
                        value = "₹${String.format("%.0f", totalSales)}",
                        icon = Icons.Filled.Add,
                        color = SuccessGreen,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ProfessionalKPICard(
                        label = "Total Purchases",
                        value = "₹${String.format("%.0f", totalPurchases)}",
                        icon = Icons.Filled.Delete,
                        color = ErrorRed,
                        modifier = Modifier.weight(1f)
                    )
                    ProfessionalKPICard(
                        label = "Profit/Loss",
                        value = "₹${String.format("%.0f", profit)}",
                        icon = Icons.Filled.Edit,
                        color = if (profit >= 0) SuccessGreen else ErrorRed,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Risk Meter - Professional Alert Box
            RiskMeterCard(
                cashInHand = cashInHand,
                pendingCheques = pendingCheques,
                isAtRisk = isAtRisk
            )

            // Quick Actions - 4 Button Grid
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    "Quick Actions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 4.dp)
                )
                ProfessionalQuickActionGrid(onNavigateTo)
            }

            // Alerts Section
            if (lowStockItems.isNotEmpty() || pendingCheques > 0) {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Alerts & Reminders",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 4.dp)
                    )

                    if (pendingCheques > 0) {
                        AlertCard(
                            icon = Icons.Filled.Warning,
                            title = "Pending Cheques",
                            message = "₹${String.format("%.0f", pendingCheques)} due",
                            color = WarningAmber
                        )
                    }

                    if (lowStockItems.isNotEmpty()) {
                        AlertCard(
                            icon = Icons.Filled.Notifications,
                            title = "Low Stock Alert",
                            message = "${lowStockItems.size} items need restocking",
                            color = ErrorRed
                        )
                    }
                }
            }

            // Recent Transactions
            if (allBills.isNotEmpty()) {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Recent Transactions",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 4.dp)
                    )

                    LazyColumn(
                        modifier = Modifier.heightIn(max = 200.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(allBills.take(5)) { bill ->
                            TransactionCard(bill)
                        }
                    }
                }
            }

            // Footer spacing
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun ProfessionalKPICard(
    label: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(120.dp)
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    label,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp
                )
                Icon(
                    icon,
                    contentDescription = label,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
            }

            Text(
                value,
                style = MaterialTheme.typography.headlineSmall,
                color = color,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }
    }
}

@Composable
private fun RiskMeterCard(
    cashInHand: Double,
    pendingCheques: Double,
    isAtRisk: Boolean
) {
    val backgroundColor = if (isAtRisk) ErrorRed.copy(alpha = 0.1f) else SuccessGreen.copy(alpha = 0.1f)
    val borderColor = if (isAtRisk) ErrorRed else SuccessGreen
    val statusColor = if (isAtRisk) ErrorRed else SuccessGreen
    val statusText = if (isAtRisk) "⚠️ HIGH RISK" else "✅ SAFE"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .shadow(2.dp, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(2.dp, borderColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Risk Meter",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
                Text(
                    statusText,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = statusColor
                )
            }

            // Risk details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Cash Available", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    Text("₹${String.format("%.0f", cashInHand)}", fontWeight = FontWeight.Bold)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Cheques Due", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    Text("₹${String.format("%.0f", pendingCheques)}", fontWeight = FontWeight.Bold)
                }
            }

            // Progress indicator
            LinearProgressIndicator(
                progress = { (pendingCheques / (cashInHand + pendingCheques).coerceAtLeast(1.0)).toFloat() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = statusColor,
                trackColor = Color.Gray.copy(alpha = 0.2f)
            )

            if (isAtRisk) {
                Text(
                    "⚠️ Insufficient cash for pending cheques. Plan ahead!",
                    style = MaterialTheme.typography.labelSmall,
                    color = ErrorRed,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun ProfessionalQuickActionGrid(onNavigateTo: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            QuickActionButton(
                "📊 Sales",
                SuccessGreen,
                modifier = Modifier.weight(1f)
            ) { onNavigateTo("sales_purchase") }
            
            QuickActionButton(
                "💳 Cheques",
                NavyPrimary,
                modifier = Modifier.weight(1f)
            ) { onNavigateTo("cheques") }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            QuickActionButton(
                "📦 Inventory",
                WarningAmber,
                modifier = Modifier.weight(1f)
            ) { onNavigateTo("inventory") }
            
            QuickActionButton(
                "📈 Reports",
                GoldAccent,
                modifier = Modifier.weight(1f)
            ) { onNavigateTo("reports") }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            QuickActionButton(
                "📋 Sale Report",
                SuccessGreen,
                modifier = Modifier.weight(1f)
            ) { onNavigateTo("sale_report") }
            
            QuickActionButton(
                "📥 Purchase",
                ErrorRed,
                modifier = Modifier.weight(1f)
            ) { onNavigateTo("purchase_report") }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            QuickActionButton(
                "💰 Receipt",
                AccountBalanceWallet,
                modifier = Modifier.weight(1f)
            ) { onNavigateTo("payment_receipt") }
            
            QuickActionButton(
                "📊 Analytics",
                Color(0xFF9C27B0),
                modifier = Modifier.weight(1f)
            ) { onNavigateTo("sales_analytics") }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            QuickActionButton(
                "📥 Import Excel",
                Color(0xFF00897B),
                modifier = Modifier.weight(1f)
            ) { onNavigateTo("excel_import") }
            
            QuickActionButton(
                "⚙️ Settings",
                Color.Gray,
                modifier = Modifier.weight(1f)
            ) { onNavigateTo("auth") }
        }
    }
}

@Composable
private fun QuickActionButton(
    label: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    ElevatedButton(
        onClick = onClick,
        modifier = modifier
            .height(70.dp)
            .shadow(4.dp, RoundedCornerShape(12.dp)),
        colors = ButtonDefaults.elevatedButtonColors(
            containerColor = color.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            label,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = color,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun AlertCard(
    icon: ImageVector,
    title: String,
    message: String,
    color: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, color)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = title,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, color = color)
                Text(message, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            }
        }
    }
}

@Composable
private fun TransactionCard(bill: com.example.munimji.data.Bill) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(1.dp, RoundedCornerShape(10.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Bill #${bill.billNumber}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Text(
                    bill.type,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (bill.type == "SALE") SuccessGreen else ErrorRed,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Text(
                "₹${String.format("%.0f", bill.totalAmount)}",
                fontWeight = FontWeight.Bold,
                color = if (bill.type == "SALE") SuccessGreen else ErrorRed,
                fontSize = 14.sp
            )
        }
    }
}
