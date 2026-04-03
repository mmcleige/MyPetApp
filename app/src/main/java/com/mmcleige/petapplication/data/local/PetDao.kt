package com.mmcleige.petapplication.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

// @Dao 告诉系统：这是专门用来操作 "pets" 表的工具箱
@Dao
interface PetDao {

    // 1. 增：把新宠物存进数据库 (如果遇到冲突，就替换掉旧的)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPet(pet: PetEntity)

    // 2. 查：获取所有的宠物档案，并按 id 倒序排列 (最新添加的排在最前面)
    // Flow 是一个非常强大的数据流：一旦数据库有更新，它会自动通知界面刷新！
    @Query("SELECT * FROM pets ORDER BY id DESC")
    fun getAllPets(): Flow<List<PetEntity>>

}
