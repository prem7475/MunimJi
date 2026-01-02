package com.example.munimji.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cheques")
data class Cheque(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    var partyName: String = "",
    var number: String = "",
    var date: String = "",
    var amount: Double = 0.0,
    var status: String = "Pending"
)
