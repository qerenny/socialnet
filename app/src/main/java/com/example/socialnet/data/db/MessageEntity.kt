package com.example.socialnet.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey val id: Int,
    val userId: Int,
    val body: String
)
