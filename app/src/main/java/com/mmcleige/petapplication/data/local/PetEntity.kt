package com.mmcleige.petapplication.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

// @Entity 告诉系统：我要在数据库里建一张叫 "pets" 的数据表
@Entity(tableName = "pets")
data class PetEntity(
    // @PrimaryKey 告诉系统：给每只宠物自动发一个唯一的身份证号 (id)
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    // 下面就是表格里的列名（宠物的各项属性）
    val name: String,
    val breed: String,
    val age: Double,
    val weight: Double
)
