@file:Suppress("DEPRECATION")
package com.example.munimji.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.munimji.data.Bill
import com.example.munimji.ui.theme.*
import com.example.munimji.ui.viewmodel.AppViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaleReportScreen(
    viewModel: AppViewModel,
    onNavigateBack: () -> Unit = {}
) {
    val allBills by viewModel.allBills.collectAsState(initial = emptyList())
    val saleBills = allBills.filter { it.type == "SALE" }
    
    var selectedDateRange by remember { mutableStateOf(Pair(0L, System.currentTimeMillis())) }
    
    val filteredBills = saleBills.filter { bill ->
        bill.date in selectedDateRange.first..selectedDateRange.second
    }
    
    val totalSales = filteredBills.sumOf { it.totalAmount }
    val totalTransactions = filteredBills.size
    val avgSaleValue = if (filteredBills.isNotEmpty()) totalSales / filteredBills.size else 0.0
    val topCustomer = filteredBills.groupBy { it.vendorName }
        .maxByOrNull { (_, bills) -> bills.sumOf { it.totalAmount } }?.key ?: "N/A"
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // Header
        TopAppBar(
            title = { Text("Sale Report", fontWeight = FontWeight.Bold) },
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
                        label = "Total Sales",
                        value = "₹${String.format("%.0f", totalSales)}",
                        icon = Icons.Filled.Add,
                        color = SuccessGreen,
                        modifier = Modifier.weight(1f)
                    )
                    ReportSummaryCard(
                        label = "Transactions",
                        value = totalTransactions.toString(),
                        icon = Icons.Filled.ShoppingCart,
                        color = NavyPrimary,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ReportSummaryCard(
                        label = "Avg Sale Value",
                        value = "₹${String.format("%.2f", avgSaleValue)}",
                        icon = Icons.Filled.Edit,
                        color = GoldAccent,
                        modifier = Modifier.weight(1f)
                    )
                    ReportSummaryCard(
                        label = "Top Customer",
                        value = topCustomer.take(10),
                        icon = Icons.Filled.Person,
                        color = AccountBalanceWallet,
                        modifier = Modifier.weight(1f)
                    )
                }
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
            
            // Sales Bills List
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
                                Icons.Filled.ShoppingCart,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = Color.Gray.copy(alpha = 0.5f)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                "No sales found",
                                color = Color.Gray,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            } else {
                items(filteredBills) { bill ->
                    SaleBillCard(bill)
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
private fun SaleBillCard(bill: Bill) {
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
                    color = SuccessGreen
                )
            }
            
            // Bill Details
            HorizontalDivider()
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Date: $dateStr", fontSize = 12.sp, color = Color.Gray)
                Text("Status: ${bill.status}", fontSize = 12.sp, color = SuccessGreen)
            }
        }
    }
}
