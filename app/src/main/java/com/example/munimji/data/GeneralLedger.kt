package com.example.munimji.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "general_ledger")
data class GeneralLedger(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    var date: Date,
    var description: String,
    var accountType: String, // Asset, Liability, Income, Expense, Equity
    var accountName: String,
    var debit: Double = 0.0,
    var credit: Double = 0.0,
    var balance: Double = 0.0,
    var transactionId: String? = null // Reference to original transaction
)
