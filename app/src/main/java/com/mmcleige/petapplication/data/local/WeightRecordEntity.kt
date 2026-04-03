package com.mmcleige.petapplication.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

// 告诉系统：在底层小黑屋里，再给我建一张叫 "weight_records" 的新表
@Entity(tableName = "weight_records")
data class WeightRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,               // 每条记录的唯一编号
    val weight: Double,            // 记录的体重数值 (比如 15.5)
    val timestamp: Long = System.currentTimeMillis() // 记录的时间戳（默认自动获取当前手机时间）
)
