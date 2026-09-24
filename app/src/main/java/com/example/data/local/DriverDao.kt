package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.DriverKyc
import kotlinx.coroutines.flow.Flow

@Dao
interface DriverDao {
    @Query("SELECT * FROM driver_kyc ORDER BY submissionDate DESC")
    fun getAllDrivers(): Flow<List<DriverKyc>>

    @Query("SELECT * FROM driver_kyc WHERE driverId = :driverId LIMIT 1")
    fun getDriverById(driverId: String): Flow<DriverKyc?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDriver(driver: DriverKyc)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDrivers(drivers: List<DriverKyc>)

    @Update
    suspend fun updateDriver(driver: DriverKyc)

    @Query("UPDATE driver_kyc SET status = :status WHERE driverId = :driverId")
    suspend fun updateKycStatus(driverId: String, status: String)
}
