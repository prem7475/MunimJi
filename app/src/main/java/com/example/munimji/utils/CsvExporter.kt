package com.example.munimji.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.munimji.data.Bill
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object CsvExporter {
    private val dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())

    fun exportBillsToCsv(context: Context, bills: List<Bill>, fileName: String = "bills_export.csv"): Uri? {
        return try {
            val file = File(context.getExternalFilesDir(null), fileName)
            file.deleteOnExit()

            file.bufferedWriter().use { writer ->
                // Write header
                writer.write("Bill Number,Type,Customer/Vendor,Date,Amount,Tax %,Total,Payment Mode,Status\n")

                // Write bill data
                bills.forEach { bill ->
                    val customerVendor = if (bill.type == "SALE") "Customer #${bill.customerId}" else bill.vendorName
                    writer.write(
                        "\"${bill.billNumber}\",\"${bill.type}\",\"${customerVendor}\",\"${dateFormat.format(bill.date)}\",\"${bill.totalAmount}\",\"${bill.taxPercentage}\",\"${bill.totalAmount + bill.taxAmount}\",\"${bill.paymentMode}\",\"${bill.status}\"\n"
                    )
                }
            }

            // Return file URI for sharing
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
        } catch (e: Exception) {
            null
        }
    }

    fun shareCsvFile(context: Context, fileUri: Uri, fileName: String) {
        try {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/csv"
                putExtra(Intent.EXTRA_STREAM, fileUri)
                putExtra(Intent.EXTRA_SUBJECT, "Business Records Export")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, "Share CSV File"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun generateFinancialSummaryCSV(
        context: Context,
        bills: List<Bill>,
        fileName: String = "financial_summary.csv"
    ): Uri? {
        return try {
            val file = File(context.getExternalFilesDir(null), fileName)
            file.deleteOnExit()

            val salesBills = bills.filter { it.type == "SALE" }
            val purchaseBills = bills.filter { it.type == "PURCHASE" }

            val totalSales = salesBills.sumOf { it.totalAmount }
            val totalPurchases = purchaseBills.sumOf { it.totalAmount }
            val totalSalesTax = salesBills.sumOf { it.totalAmount * it.taxPercentage / 100 }
            val totalPurchasesTax = purchaseBills.sumOf { it.totalAmount * it.taxPercentage / 100 }

            file.bufferedWriter().use { writer ->
                writer.write("Financial Summary Report\n")
                writer.write("Generated: ${dateFormat.format(Date())}\n\n")

                writer.write("SALES\n")
                writer.write("Total Sales,${totalSales}\n")
                writer.write("Sales Tax,${totalSalesTax}\n")
                writer.write("Number of Sales,${salesBills.size}\n\n")

                writer.write("PURCHASES\n")
                writer.write("Total Purchases,${totalPurchases}\n")
                writer.write("Purchase Tax,${totalPurchasesTax}\n")
                writer.write("Number of Purchases,${purchaseBills.size}\n\n")

                writer.write("SUMMARY\n")
                writer.write("Gross Profit/Loss,${totalSales - totalPurchases}\n")
                writer.write("Total Tax,${totalSalesTax + totalPurchasesTax}\n")
                writer.write("Net Profit/Loss,${totalSales - totalPurchases - (totalSalesTax + totalPurchasesTax)}\n")
            }

            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
        } catch (e: Exception) {
            null
        }
    }
}
