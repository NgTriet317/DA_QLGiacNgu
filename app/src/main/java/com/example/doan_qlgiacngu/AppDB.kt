package com.example.doan_qlgiacngu

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

import kotlin.jvm.java

@Database(
    entities = [
        timeSleep::class,
        timeAwake::class,
        SleepExtraEntity::class,
        tgngu::class,
        userData::class,
        lienKet::class,
        muctieuData::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun timeSleepDao(): timeSleepDao
    abstract fun timeAwakeDao(): timeAwakeDao
    abstract fun sleepExtraDao(): SleepExtraDao
    abstract fun tgnguDao(): tgnguDao
    abstract fun userDataDao(): userDataDao
    abstract fun lienKetDao(): lienKetDao

    abstract fun muctieuDataDao(): muctieuDataDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun get(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "sleep_app.db"
                )
                    .allowMainThreadQueries() // chỉ dùng khi học / demo
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}