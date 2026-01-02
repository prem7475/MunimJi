package com.example.munimji.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "inventory")
data class InventoryItem(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    var name: String,
    var quantity: Int,
    var buyPrice: Double,
    var sellPrice: Double,
    var barcode: String = ""
)
