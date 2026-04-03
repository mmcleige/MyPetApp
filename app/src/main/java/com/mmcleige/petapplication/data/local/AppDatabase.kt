package com.mmcleige.petapplication.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// 👇 版本号改成 2
@Database(entities = [PetEntity::class, WeightRecordEntity::class], version = 5, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun petDao(): PetDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "pet_database"
                )
                    .fallbackToDestructiveMigration() // 👇 告诉系统：表结构变了就直接删了旧数据重建，别报错
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
