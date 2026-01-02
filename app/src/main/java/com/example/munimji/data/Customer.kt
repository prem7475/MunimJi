package com.example.munimji.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "customers")
data class Customer(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    var name: String,
    var totalDue: Double = 0.0
)
