package com.mmcleige.petapplication.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PetDao {

    // 🌟 技能1：存入新宠物时，返回系统给它分配的专属 ID 号！
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPet(pet: PetEntity): Long

    // 🌟 技能2：直接获取最新添加的一只宠物（用 Flow 实时监听）
    @Query("SELECT * FROM pets ORDER BY id DESC LIMIT 1")
    fun getLatestPetFlow(): Flow<PetEntity?>

    // 🌟 技能3：同步更新！修改指定宠物的最新体重
    @Query("UPDATE pets SET weight = :newWeight WHERE id = :petId")
    suspend fun updatePetWeight(petId: Int, newWeight: Double)

    @Query("SELECT * FROM pets ORDER BY id DESC")
    fun getAllPets(): Flow<List<PetEntity>>

    @Insert
    suspend fun insertWeightRecord(record: WeightRecordEntity)

    // 🌟 技能4：定向查询！只查“某一只专属宠物”的历史体重
    @Query("SELECT * FROM weight_records WHERE petId = :petId ORDER BY timestamp ASC")
    fun getWeightRecordsForPet(petId: Int): Flow<List<WeightRecordEntity>>
}
