package com.example.munimji.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "bills")
data class Bill(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    var billNumber: String = "",
    var type: String = "", // "SALE" or "PURCHASE"
    var customerId: Long = 0, // For sales
    var vendorName: String = "", // For purchases
    var date: Long = System.currentTimeMillis(),
    var totalAmount: Double = 0.0,
    var taxPercentage: Double = 0.0,
    var taxAmount: Double = 0.0,
    var paymentMode: String = "CASH", // "CASH" or "CREDIT"
    var notes: String = "",
    var billImageUrl: String = "",
    var status: String = "COMPLETED" // "DRAFT", "COMPLETED", "CANCELLED"
)

@Entity(tableName = "bill_items")
data class BillItem(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    var billId: Long = 0,
    var itemId: Long = 0,
    var itemName: String = "",
    var barcode: String = "",
    var quantity: Double = 1.0,
    var unitPrice: Double = 0.0,
    var itemTotal: Double = 0.0
)
