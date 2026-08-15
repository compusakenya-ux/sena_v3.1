package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_places")
data class SavedPlace(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val label: String, // Home, Work, Nyali Beach
    val address: String,
    val iconName: String
)
