package com.example.munimji.utils

import java.text.SimpleDateFormat
import java.util.*

/**
 * Payment Receipt Generator - Creates formatted receipts for payments
 */
object ReceiptGenerator {

    /**
     * Generate payment receipt text
     */
    fun generatePaymentReceipt(
        receiptNumber: String,
        paymentDate: Long,
        payer: String,
        amount: Double,
        paymentMethod: String,
        description: String,
        referenceNumber: String = "",
        shopName: String = "MunimJi Shop"
    ): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())
        val dateStr = sdf.format(Date(paymentDate))
        
        val sb = StringBuilder()
        
        // Header
        sb.append("═".repeat(50)).append("\n")
        sb.append(shopName.padStart((shopName.length + 50) / 2).padEnd(50)).append("\n")
        sb.append("PAYMENT RECEIPT".padStart((15 + 50) / 2).padEnd(50)).append("\n")
        sb.append("═".repeat(50)).append("\n\n")
        
        // Receipt Details
        sb.append(String.format("%-20s: %s\n", "Receipt #", receiptNumber))
        sb.append(String.format("%-20s: %s\n", "Date & Time", dateStr))
        sb.append(String.format("%-20s: %s\n", "Payer Name", payer))
        sb.append("─".repeat(50)).append("\n\n")
        
        // Payment Details
        sb.append(String.format("%-20s: ₹%.2f\n", "Amount Received", amount))
        sb.append(String.format("%-20s: %s\n", "Payment Method", paymentMethod))
        
        if (description.isNotEmpty()) {
            sb.append(String.format("%-20s: %s\n", "Description", description))
        }
        
        if (referenceNumber.isNotEmpty()) {
            sb.append(String.format("%-20s: %s\n", "Reference #", referenceNumber))
        }
        
        sb.append("─".repeat(50)).append("\n\n")
        
        // Footer
        sb.append("Authorized By: MunimJi\n")
        sb.append("Thank you for the payment!\n")
        sb.append("═".repeat(50)).append("\n")
        
        return sb.toString()
    }

    /**
     * Generate cheque receipt
     */
    fun generateChequeReceipt(
        receiptNumber: String,
        chequeNumber: String,
        chequeDate: Long,
        amount: Double,
        bankName: String,
        drawerName: String,
        shopName: String = "MunimJi Shop"
    ): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val chequeDateStr = sdf.format(Date(chequeDate))
        val receiptDateStr = sdf.format(Date(System.currentTimeMillis()))
        
        val sb = StringBuilder()
        
        // Header
        sb.append("═".repeat(50)).append("\n")
        sb.append(shopName.padStart((shopName.length + 50) / 2).padEnd(50)).append("\n")
        sb.append("CHEQUE RECEIPT".padStart((14 + 50) / 2).padEnd(50)).append("\n")
        sb.append("═".repeat(50)).append("\n\n")
        
        // Receipt Details
        sb.append(String.format("%-20s: %s\n", "Receipt #", receiptNumber))
        sb.append(String.format("%-20s: %s\n", "Date Received", receiptDateStr))
        sb.append("─".repeat(50)).append("\n\n")
        
        // Cheque Details
        sb.append("CHEQUE DETAILS:\n")
        sb.append(String.format("%-20s: %s\n", "Cheque #", chequeNumber))
        sb.append(String.format("%-20s: %s\n", "Cheque Date", chequeDateStr))
        sb.append(String.format("%-20s: %s\n", "Bank Name", bankName))
        sb.append(String.format("%-20s: ₹%.2f\n", "Amount", amount))
        sb.append(String.format("%-20s: %s\n", "Drawer Name", drawerName))
        sb.append("─".repeat(50)).append("\n\n")
        
        // Footer
        sb.append("⚠️  Please encash before cheque expiry date\n")
        sb.append("Thank you!\n")
        sb.append("═".repeat(50)).append("\n")
        
        return sb.toString()
    }

    /**
     * Generate customer payment receipt
     */
    fun generateCustomerPaymentReceipt(
        customerName: String,
        amount: Double,
        dueAmount: Double,
        paidDate: Long,
        invoiceNumber: String = "",
        shopName: String = "MunimJi Shop"
    ): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val dateStr = sdf.format(Date(paidDate))
        val remainingAmount = dueAmount - amount
        
        val sb = StringBuilder()
        
        // Header
        sb.append("═".repeat(50)).append("\n")
        sb.append(shopName.padStart((shopName.length + 50) / 2).padEnd(50)).append("\n")
        sb.append("PAYMENT ACKNOWLEDGEMENT".padStart((23 + 50) / 2).padEnd(50)).append("\n")
        sb.append("═".repeat(50)).append("\n\n")
        
        // Customer Details
        sb.append(String.format("%-25s: %s\n", "Customer", customerName))
        if (invoiceNumber.isNotEmpty()) {
            sb.append(String.format("%-25s: %s\n", "Invoice #", invoiceNumber))
        }
        sb.append(String.format("%-25s: %s\n", "Payment Date", dateStr))
        sb.append("─".repeat(50)).append("\n\n")
        
        // Amount Details
        sb.append(String.format("%-25s: ₹%.2f\n", "Amount Paid", amount))
        sb.append(String.format("%-25s: ₹%.2f\n", "Previous Due", dueAmount))
        sb.append(String.format("%-25s: ₹%.2f\n", "Outstanding Due", remainingAmount.coerceAtLeast(0.0)))
        sb.append("─".repeat(50)).append("\n\n")
        
        // Footer
        if (remainingAmount <= 0) {
            sb.append("✓ Account fully settled. Thank you!\n")
        } else {
            sb.append("Remaining balance due. Please settle at earliest.\n")
        }
        sb.append("═".repeat(50)).append("\n")
        
        return sb.toString()
    }
}
