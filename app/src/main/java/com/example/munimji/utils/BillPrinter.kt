package com.example.munimji.utils

import android.content.Context
import android.os.Environment
import android.util.Log
import com.example.munimji.data.Bill
import com.example.munimji.data.BillItem
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * PDF Bill Printer - Generates formatted bill PDFs
 * Uses text-based PDF generation for compatibility
 */
object BillPrinter {
    private const val TAG = "BillPrinter"
    
    /**
     * Generate bill text content for printing/sharing
     */
    fun generateBillText(
        bill: Bill,
        items: List<BillItem>,
        shopName: String = "MunimJi Shop",
        shopAddress: String = "",
        shopPhone: String = ""
    ): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())
        val dateStr = sdf.format(Date(bill.date))
        
        val sb = StringBuilder()
        
        // Header
        sb.append("═".repeat(50)).append("\n")
        sb.append(shopName.center(50)).append("\n")
        if (shopAddress.isNotEmpty()) {
            sb.append(shopAddress.center(50)).append("\n")
        }
        if (shopPhone.isNotEmpty()) {
            sb.append("📞 $shopPhone".center(50)).append("\n")
        }
        sb.append("═".repeat(50)).append("\n\n")
        
        // Invoice Details
        sb.append("Bill #: ${bill.billNumber}        Date: $dateStr\n")
        sb.append("Customer: ${bill.vendorName}\n")
        sb.append("Type: ${bill.type}\n")
        sb.append("─".repeat(50)).append("\n\n")
        
        // Item Table Header
        sb.append(String.format("%-5s %-25s %10s %8s %12s\n", "Item", "Description", "Qty", "Price", "Amount"))
        sb.append("─".repeat(50)).append("\n")
        
        // Items
        var totalAmount = 0.0
        items.forEachIndexed { index, item ->
            val amount = item.quantity * item.unitPrice
            totalAmount += amount
            sb.append(String.format(
                "%-5d %-25s %10.0f %8.2f ₹%10.2f\n",
                index + 1,
                item.itemName.take(25),
                item.quantity,
                item.unitPrice,
                amount
            ))
        }
        
        sb.append("─".repeat(50)).append("\n")
        sb.append(String.format("%45s ₹%10.2f\n", "TOTAL:", totalAmount))
        sb.append("═".repeat(50)).append("\n\n")
        
        // Footer
        sb.append("Thank you for your business!\n")
        sb.append("Please retain this bill for your records.\n")
        sb.append("═".repeat(50)).append("\n")
        
        return sb.toString()
    }

    /**
     * Save bill as text file and return file path
     */
    fun saveBillAsText(
        bill: Bill,
        items: List<BillItem>,
        context: Context,
        shopName: String = "MunimJi Shop"
    ): String? {
        return try {
            val billText = generateBillText(bill, items, shopName)
            val fileName = "Bill_${bill.billNumber}_${System.currentTimeMillis()}.txt"
            
            val file = File(
                context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),
                fileName
            )
            
            file.writeText(billText, Charsets.UTF_8)
            Log.d(TAG, "Bill saved: ${file.absolutePath}")
            file.absolutePath
        } catch (e: Exception) {
            Log.e(TAG, "Error saving bill", e)
            null
        }
    }

    /**
     * Format bill for email/sharing
     */
    fun formatBillForSharing(
        bill: Bill,
        items: List<BillItem>,
        shopName: String = "MunimJi Shop"
    ): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())
        val dateStr = sdf.format(Date(bill.date))
        
        val sb = StringBuilder()
        sb.append("$shopName Bill\n")
        sb.append("─".repeat(50)).append("\n")
        sb.append("Bill #: ${bill.billNumber}\n")
        sb.append("Date: $dateStr\n")
        sb.append("Customer: ${bill.vendorName}\n")
        sb.append("Type: ${bill.type}\n\n")
        
        sb.append("Items:\n")
        var totalAmount = 0.0
        items.forEachIndexed { index, item ->
            val amount = item.quantity * item.unitPrice
            totalAmount += amount
            sb.append("${index + 1}. ${item.itemName} - ₹${item.unitPrice} x ${item.quantity} = ₹$amount\n")
        }
        
        sb.append("\nTotal Amount: ₹$totalAmount\n")
        return sb.toString()
    }

    private fun String.center(width: Int): String {
        val padding = (width - this.length) / 2
        return " ".repeat(padding.coerceAtLeast(0)) + this
    }
}
