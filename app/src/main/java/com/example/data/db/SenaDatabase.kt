package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [RideBooking::class, WalletTransaction::class, SavedPlace::class],
    version = 1,
    exportSchema = false
)
abstract class SenaDatabase : RoomDatabase() {
    abstract fun rideDao(): RideDao
    abstract fun walletDao(): WalletDao
    abstract fun savedPlaceDao(): SavedPlaceDao

    companion object {
        @Volatile
        private var INSTANCE: SenaDatabase? = null

        fun getDatabase(context: Context): SenaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SenaDatabase::class.java,
                    "sena_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
