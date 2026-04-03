package com.mmcleige.petapplication.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pets")
data class PetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val breed: String,
    // 👇 重点修改：删掉 age，换成 birthDate (保存出生那天的时间戳)
    val birthDate: Long,
    val weight: Double,
    val avatarUri: String? = null
)
