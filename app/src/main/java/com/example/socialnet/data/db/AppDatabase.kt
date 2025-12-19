package com.example.socialnet.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.socialnet.data.db.MessageEntity

@Database(entities = [MessageEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun messageDao(): MessageDao
}
