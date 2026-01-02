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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.munimji.data.Bill
import com.example.munimji.ui.theme.*
import com.example.munimji.ui.viewmodel.AppViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PurchaseReportScreen(
    viewModel: AppViewModel,
    onNavigateBack: () -> Unit = {}
) {
    val allBills by viewModel.allBills.collectAsState(initial = emptyList())
    val purchaseBills = allBills.filter { it.type == "PURCHASE" }
    
    var selectedDateRange by remember { mutableStateOf(Pair(0L, System.currentTimeMillis())) }
    
    val filteredBills = purchaseBills.filter { bill ->
        bill.date in selectedDateRange.first..selectedDateRange.second
    }
    
    val totalPurchases = filteredBills.sumOf { it.totalAmount }
    val totalItems: Long = filteredBills.count().toLong()
    val avgPurchaseValue = if (filteredBills.isNotEmpty()) totalPurchases / filteredBills.size else 0.0
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // Header
        TopAppBar(
            title = { Text("Purchase Report", fontWeight = FontWeight.Bold) },
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
        
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Summary Cards
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ReportSummaryCard(
                        label = "Total Purchases",
                        value = "₹${String.format("%.0f", totalPurchases)}",
                        icon = Icons.Filled.ShoppingCart,
                        color = ErrorRed,
                        modifier = Modifier.weight(1f)
                    )
                    ReportSummaryCard(
                        label = "Transactions",
                        value = totalItems.toString(),
                        icon = Icons.Filled.Delete,
                        color = NavyPrimary,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            
            item {
                ReportSummaryCard(
                    label = "Average Purchase",
                    value = "₹${String.format("%.2f", avgPurchaseValue)}",
                    icon = Icons.Filled.Edit,
                    color = GoldAccent,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            // Filter Section
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(2.dp, RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            "Filter by Period",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ElevatedButton(
                                onClick = { 
                                    val calendar = Calendar.getInstance()
                                    calendar.set(Calendar.DAY_OF_MONTH, 1)
                                    selectedDateRange = Pair(calendar.timeInMillis, System.currentTimeMillis())
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.elevatedButtonColors(
                                    containerColor = SuccessGreen.copy(alpha = 0.2f)
                                )
                            ) {
                                Text("This Month", color = SuccessGreen, fontSize = 12.sp)
                            }
                            
                            ElevatedButton(
                                onClick = {
                                    val calendar = Calendar.getInstance()
                                    calendar.add(Calendar.MONTH, -1)
                                    selectedDateRange = Pair(calendar.timeInMillis, System.currentTimeMillis())
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.elevatedButtonColors(
                                    containerColor = WarningAmber.copy(alpha = 0.2f)
                                )
                            ) {
                                Text("Last 30 Days", color = WarningAmber, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
            
            // Purchase Bills List
            if (filteredBills.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Filled.ShoppingCart,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = Color.Gray.copy(alpha = 0.5f)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                "No purchases found",
                                color = Color.Gray,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            } else {
                items(filteredBills) { bill ->
                    PurchaseBillCard(bill)
                }
            }
        }
    }
}

@Composable
private fun ReportSummaryCard(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .shadow(2.dp, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(40.dp),
                tint = color
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(label, fontSize = 12.sp, color = Color.Gray)
                Text(value, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        }
    }
}

@Composable
private fun PurchaseBillCard(bill: Bill) {
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val dateStr = sdf.format(Date(bill.date))
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Bill Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Bill #${bill.billNumber}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(bill.vendorName, fontSize = 13.sp, color = Color.Gray)
                }
                Text(
                    "₹${String.format("%.2f", bill.totalAmount)}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = ErrorRed
                )
            }
            
            // Bill Details
            HorizontalDivider()
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Date: $dateStr", fontSize = 12.sp, color = Color.Gray)
                Text("Items: 1", fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}
