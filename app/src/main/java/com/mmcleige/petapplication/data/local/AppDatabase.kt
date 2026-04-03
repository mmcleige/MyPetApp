package com.mmcleige.petapplication.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// version = 1 表示当前是第一版数据库，以后如果增加新字段就升级 version
@Database(entities = [PetEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    // 把刚才写的 DAO 工具箱暴露出来供外部使用
    abstract fun petDao(): PetDao

    // 伴生对象 (相当于 Java 的 static)，保证全 APP 只有一个数据库实例
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "pet_database" // 这是保存在手机里的数据库文件名
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
