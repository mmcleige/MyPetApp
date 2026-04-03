package com.mmcleige.petapplication.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pets")
data class PetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val breed: String,
    val age: Double,
    val weight: Double,
    // 👇 新增这一列：保存照片在手机里的系统地址。加了 ? 表示允许为空（不强制必须上传照片）
    val avatarUri: String? = null
)
