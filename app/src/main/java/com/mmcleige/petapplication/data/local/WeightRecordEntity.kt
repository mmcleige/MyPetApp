package com.mmcleige.petapplication.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weight_records")
data class WeightRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val petId: Int, // 🌟 新增：专属标签！这属于哪只宠物？
    val weight: Double,
    val timestamp: Long = System.currentTimeMillis()
)
