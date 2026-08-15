package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface RideDao {
    @Query("SELECT * FROM ride_bookings ORDER BY timestamp DESC")
    fun getAllRides(): Flow<List<RideBooking>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRide(ride: RideBooking): Long

    @Update
    suspend fun updateRide(ride: RideBooking)

    @Query("SELECT * FROM ride_bookings WHERE id = :rideId LIMIT 1")
    suspend fun getRideById(rideId: Long): RideBooking?
}
