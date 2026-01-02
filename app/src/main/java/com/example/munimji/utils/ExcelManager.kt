package com.example.munimji.utils

import android.content.Context
import android.net.Uri
import com.example.munimji.data.InventoryItem
import com.example.munimji.data.Transaction
import com.opencsv.CSVReader
import com.opencsv.CSVWriter
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

object ExcelManager {

    /**
     * Import inventory items from CSV/Excel file
     */
    fun importItemsFromExcel(context: Context, uri: Uri): List<InventoryItem> {
        val items = mutableListOf<InventoryItem>()
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val reader = CSVReader(InputStreamReader(inputStream))
            var nextLine: Array<String>?
            var isHeader = true

            while (reader.readNext().also { nextLine = it } != null) {
                if (isHeader) {
                    isHeader = false
                    continue
                }

                val line = nextLine ?: continue
                if (line.size < 4) continue

                val name = line[0].trim().takeIf { it.isNotEmpty() } ?: continue
                val quantity = line[1].trim().toIntOrNull() ?: 0
                val buyPrice = line[2].trim().toDoubleOrNull() ?: 0.0
                val sellPrice = line[3].trim().toDoubleOrNull() ?: 0.0
                val barcode = if (line.size > 4) line[4].trim() else ""

                items.add(
                    InventoryItem(
                        name = name,
                        quantity = quantity,
                        buyPrice = buyPrice,
                        sellPrice = sellPrice,
                        barcode = barcode
                    )
                )
            }
            reader.close()
            items
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * Export inventory to CSV file
     */
    fun exportInventoryToCSV(
        context: Context,
        items: List<InventoryItem>,
        fileName: String = "Inventory_${System.currentTimeMillis()}.csv"
    ): File? {
        return try {
            val file = File(context.getExternalFilesDir(null), fileName)
            val writer = CSVWriter(FileWriter(file))

            // Header
            val headers = arrayOf("Name", "Quantity", "BuyPrice", "SellPrice", "Barcode")
            writer.writeNext(headers)

            items.forEach { item ->
                val row = arrayOf(
                    item.name,
                    item.quantity.toString(),
                    item.buyPrice.toString(),
                    item.sellPrice.toString(),
                    item.barcode
                )
                writer.writeNext(row)
            }

            writer.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Export sales report to CSV file
     */
    fun exportSalesReportToExcel(
        context: Context,
        transactions: List<Transaction>,
        fileName: String = "Sales_Report_${System.currentTimeMillis()}.csv"
    ): File? {
        return try {
            val file = File(context.getExternalFilesDir(null), fileName)
            val writer = CSVWriter(FileWriter(file))

            // Write header
            val headers = arrayOf("Date", "Party Name", "Bill No", "Amount", "GST Rate", "GST Amount", "Total with Tax", "Payment Method")
            writer.writeNext(headers)

            // Write data
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            
            transactions.filter { it.type == "Sale" }.forEach { transaction ->
                val row = arrayOf(
                    dateFormat.format(transaction.date),
                    transaction.partyName,
                    transaction.billNo,
                    transaction.amount.toString(),
                    transaction.gstRate.toString(),
                    transaction.gstAmount.toString(),
                    transaction.totalWithTax.toString(),
                    transaction.paymentMethod
                )
                writer.writeNext(row)
            }

            writer.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Export GST report to CSV file
     */
    fun exportGSTReportToExcel(
        context: Context,
        transactions: List<Transaction>,
        fileName: String = "GST_Report_${System.currentTimeMillis()}.csv"
    ): File? {
        return try {
            val file = File(context.getExternalFilesDir(null), fileName)
            val writer = CSVWriter(FileWriter(file))

            // Write header
            val headers = arrayOf("Date", "Bill No", "Sales Amount", "CGST", "SGST", "IGST", "Total Tax", "Total with Tax")
            writer.writeNext(headers)

            // Write data
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            var totalSales = 0.0
            var totalCGST = 0.0
            var totalSGST = 0.0
            var totalIGST = 0.0

            transactions.filter { it.type == "Sale" }.forEach { transaction ->
                val row = arrayOf(
                    dateFormat.format(transaction.date),
                    transaction.billNo,
                    transaction.amount.toString(),
                    transaction.cgst.toString(),
                    transaction.sgst.toString(),
                    transaction.igst.toString(),
                    transaction.gstAmount.toString(),
                    transaction.totalWithTax.toString()
                )
                writer.writeNext(row)

                totalSales += transaction.amount
                totalCGST += transaction.cgst
                totalSGST += transaction.sgst
                totalIGST += transaction.igst
            }

            // Write total row
            val totalRow = arrayOf(
                "TOTAL",
                "",
                totalSales.toString(),
                totalCGST.toString(),
                totalSGST.toString(),
                totalIGST.toString(),
                (totalCGST + totalSGST + totalIGST).toString(),
                ""
            )
            writer.writeNext(totalRow)

            writer.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Calculate total sales from transactions
     */
    fun calculateTotalSales(transactions: List<Transaction>): Double {
        return transactions.filter { it.type == "Sale" }.sumOf { it.totalWithTax }
    }

    /**
     * Calculate total GST from transactions
     */
    fun calculateTotalGST(transactions: List<Transaction>): Double {
        return transactions.filter { it.type == "Sale" }.sumOf { it.gstAmount }
    }
}
