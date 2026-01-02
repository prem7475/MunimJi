@file:Suppress("DEPRECATION")
package com.example.munimji.ui.screens

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.munimji.data.AppRepository
import com.example.munimji.data.Transaction
import com.example.munimji.ui.theme.NavyPrimary
import com.example.munimji.ui.theme.SuccessGreen
import com.example.munimji.utils.ExcelManager
import kotlinx.coroutines.launch
import androidx.compose.material3.ExperimentalMaterial3Api
import java.text.SimpleDateFormat
import java.util.*

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun SalesAnalyticsScreen(
    navController: NavController,
    repository: AppRepository
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var transactions by remember { mutableStateOf<List<Transaction>>(emptyList()) }

    LaunchedEffect(Unit) {
        scope.launch {
            repository.allTransactions.collect { transactionList ->
                transactions = transactionList
            }
        }
    }

    val salesTransactions = transactions.filter { it.type == "Sale" }
    val totalSales = ExcelManager.calculateTotalSales(salesTransactions)
    val totalGST = ExcelManager.calculateTotalGST(salesTransactions)
    val netSales = totalSales - totalGST

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF5F5F5))) {
        TopAppBar(
            title = { Text("Sales Analytics", fontWeight = FontWeight.Bold) },
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

        LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item {
                SummaryCard("Total Sales", "₹${String.format("%.2f", totalSales)}", SuccessGreen)
            }
            item {
                SummaryCard("Total GST", "₹${String.format("%.2f", totalGST)}", Color(0xFF2196F3))
            }
            item {
                SummaryCard("Net Sales", "₹${String.format("%.2f", netSales)}", Color(0xFFFFC107))
            }
            item {
                SummaryCard("Transactions", "${salesTransactions.size}", Color(0xFFFF9800))
            }

            item {
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Export Reports", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                scope.launch {
                                    val file = ExcelManager.exportSalesReportToExcel(context, salesTransactions)
                                    if (file != null) shareFile(context, file)
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(45.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Filled.Share, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Export Sales Report", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                scope.launch {
                                    val file = ExcelManager.exportGSTReportToExcel(context, salesTransactions)
                                    if (file != null) shareFile(context, file)
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(45.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9C27B0)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Filled.Share, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Export GST Report", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            if (salesTransactions.isNotEmpty()) {
                item {
                    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("GST Breakdown", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(12.dp))
                            val totalCGST = salesTransactions.sumOf { it.cgst }
                            val totalSGST = salesTransactions.sumOf { it.sgst }
                            val totalIGST = salesTransactions.sumOf { it.igst }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("CGST", fontSize = 12.sp); Text("₹${String.format("%.2f", totalCGST)}", fontSize = 12.sp, color = Color.Gray)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("SGST", fontSize = 12.sp); Text("₹${String.format("%.2f", totalSGST)}", fontSize = 12.sp, color = Color.Gray)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("IGST", fontSize = 12.sp); Text("₹${String.format("%.2f", totalIGST)}", fontSize = 12.sp, color = Color.Gray)
                            }
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Total Tax", fontWeight = FontWeight.Bold); Text("₹${String.format("%.2f", totalCGST + totalSGST + totalIGST)}", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            item { Text("Recent Sales", fontWeight = FontWeight.Bold, fontSize = 14.sp) }

            items(salesTransactions.take(10)) { transaction ->
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Row(modifier = Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(transaction.partyName, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text("Bill: ${transaction.billNo}", fontSize = 10.sp, color = Color.Gray)
                            Text(SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(transaction.date), fontSize = 10.sp, color = Color.Gray)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("₹${String.format("%.2f", transaction.totalWithTax)}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text("GST: ₹${String.format("%.2f", transaction.gstAmount)}", fontSize = 10.sp, color = Color.Gray)
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(20.dp)) }
        }
    }
}

@Composable
private fun SummaryCard(title: String, value: String, color: Color) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontSize = 12.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(4.dp))
                Text(value, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = color)
            }
            Icon(Icons.Filled.MoreVert, contentDescription = null, modifier = Modifier.size(40.dp), tint = color)
        }
    }
}

private fun shareFile(context: Context, file: java.io.File) {
    try {
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Share Report"))
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
