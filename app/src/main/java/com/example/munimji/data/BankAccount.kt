package com.example.munimji.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bank_accounts")
data class BankAccount(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    var name: String,
    var accountNumber: String,
    var bankName: String,
    var balance: Double = 0.0,
    var ifscCode: String = ""
)
