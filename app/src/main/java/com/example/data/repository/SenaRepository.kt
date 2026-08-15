package com.example.data.repository

import com.example.data.db.RideBooking
import com.example.data.db.RideDao
import com.example.data.db.SavedPlace
import com.example.data.db.SavedPlaceDao
import com.example.data.db.WalletDao
import com.example.data.db.WalletTransaction
import kotlinx.coroutines.flow.Flow

class SenaRepository(
    private val rideDao: RideDao,
    private val walletDao: WalletDao,
    private val savedPlaceDao: SavedPlaceDao
) {
    val allRides: Flow<List<RideBooking>> = rideDao.getAllRides()
    val allTransactions: Flow<List<WalletTransaction>> = walletDao.getAllTransactions()
    val savedPlaces: Flow<List<SavedPlace>> = savedPlaceDao.getAllSavedPlaces()

    suspend fun insertRide(ride: RideBooking): Long = rideDao.insertRide(ride)

    suspend fun updateRide(ride: RideBooking) = rideDao.updateRide(ride)

    suspend fun getRideById(id: Long): RideBooking? = rideDao.getRideById(id)

    suspend fun insertTransaction(transaction: WalletTransaction): Long =
        walletDao.insertTransaction(transaction)

    suspend fun seedSavedPlaces(places: List<SavedPlace>) =
        savedPlaceDao.insertAll(places)
}
