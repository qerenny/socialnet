package com.example.socialnet.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MessageDao {
    @Query("SELECT * FROM messages")
    suspend fun getAllMessages(): List<MessageEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(messages: List<MessageEntity>)

    @Query("DELETE FROM messages")
    suspend fun deleteAll()

    @androidx.room.Transaction
    suspend fun updateMessages(messages: List<MessageEntity>) {
        deleteAll()
        insertAll(messages)
    }
}
