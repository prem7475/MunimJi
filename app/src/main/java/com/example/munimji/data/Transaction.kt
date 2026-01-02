package com.example.munimji.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "transaction_table")
data class Transaction(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    var type: String, // Sale, Purchase, Expense, Income
    var partyName: String,
    var billNo: String,
    var amount: Double,
    var isCredit: Boolean = false,
    var date: Date,
    var itemName: String = "",
    // GST and Tax fields
    var gstRate: Double = 18.0, // Default GST rate
    var gstAmount: Double = 0.0,
    var cgst: Double = 0.0,
    var sgst: Double = 0.0,
    var igst: Double = 0.0,
    var totalWithTax: Double = 0.0,
    var discount: Double = 0.0,
    var paymentMethod: String = "Cash", // Cash, Bank, UPI, etc.
    var bankAccountId: Int? = null,
    var invoiceNumber: String? = null,
    var notes: String = ""
)
