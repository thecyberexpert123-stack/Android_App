package com.cyberexpert.androde.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.cyberexpert.androde.data.local.dao.UserDao
import com.cyberexpert.androde.data.local.entity.UserEntity

@Database(
    entities = [UserEntity::class],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        const val DATABASE_NAME = "app_database"
    }
}
