package com.mmcleige.petapplication.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PetDao {

    // --- 宠物基础档案任务 ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPet(pet: PetEntity)

    @Query("SELECT * FROM pets ORDER BY id DESC")
    fun getAllPets(): Flow<List<PetEntity>>

    // 👇 --- 新增：体重记录任务 --- 👇

    // 任务1：把新称的体重存进数据库
    @Insert
    suspend fun insertWeightRecord(record: WeightRecordEntity)

    // 任务2：把所有的体重记录按时间先后顺序 (ASC) 拿出来，用来画折线图！
    @Query("SELECT * FROM weight_records ORDER BY timestamp ASC")
    fun getAllWeightRecords(): Flow<List<WeightRecordEntity>>
}
