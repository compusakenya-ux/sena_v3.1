package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ride_bookings")
data class RideBooking(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val pickupLocation: String,
    val destinationLocation: String,
    val rideType: String, // Standard, Sena Fast, Electric
    val fareAmount: Double,
    val driverName: String,
    val driverVehicleNumber: String,
    val driverRating: Double,
    val status: String, // BOOKED, IN_PROGRESS, COMPLETED, CANCELLED
    val timestamp: Long = System.currentTimeMillis(),
    val ratingGiven: Float? = null,
    val reviewFeedback: String? = null
)
