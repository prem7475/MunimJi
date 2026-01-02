package com.example.munimji.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wallet")
data class Wallet(
    @PrimaryKey var id: String = "main",
    var amount: Double = 0.0
)
