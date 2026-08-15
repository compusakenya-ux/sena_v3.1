package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wallet_transactions")
data class WalletTransaction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val subtitle: String,
    val amount: Double,
    val isCredit: Boolean, // true for + (Top Up), false for - (Rides/Food)
    val status: String, // COMPLETED, SUCCESS, PENDING
    val timestamp: Long = System.currentTimeMillis()
)
