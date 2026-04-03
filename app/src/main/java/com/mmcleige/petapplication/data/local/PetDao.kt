package com.mmcleige.petapplication.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PetDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPet(pet: PetEntity): Long

    @Query("SELECT * FROM pets ORDER BY id DESC LIMIT 1")
    fun getLatestPetFlow(): Flow<PetEntity?>

    @Query("UPDATE pets SET weight = :newWeight WHERE id = :petId")
    suspend fun updatePetWeight(petId: Int, newWeight: Double)

    @Query("SELECT * FROM pets ORDER BY id DESC")
    fun getAllPets(): Flow<List<PetEntity>>

    @Insert
    suspend fun insertWeightRecord(record: WeightRecordEntity)

    @Query("SELECT * FROM weight_records WHERE petId = :petId ORDER BY timestamp ASC")
    fun getWeightRecordsForPet(petId: Int): Flow<List<WeightRecordEntity>>

    // 👇 🌟 V5.0 新增核武器：一键重置功能所需的毁灭级清理技能
    @Query("DELETE FROM pets")
    suspend fun deleteAllPets()

    @Query("DELETE FROM weight_records")
    suspend fun deleteAllWeightRecords()
}
