package com.example.socialnet.data.repository

import com.example.socialnet.data.api.ApiService
import com.example.socialnet.data.db.MessageDao
import com.example.socialnet.data.db.MessageEntity
import java.io.IOException

class MessageRepository(
    private val apiService: ApiService,
    private val messageDao: MessageDao
) {

    suspend fun getMessages(): List<MessageEntity> {
        try {
            val remotePosts = apiService.getPosts()
            val entities = remotePosts.map {
                MessageEntity(
                    id = it.id,
                    userId = it.userId,
                    body = it.body
                )
            }

            if (entities.isNotEmpty()) {
                messageDao.updateMessages(entities)
            }

            return entities
        } catch (e: Exception) {
            val localData = messageDao.getAllMessages()
            if (localData.isEmpty()) {
                if (e is IOException) {
                    return localData
                }
                throw e
            }
            return localData
        }
    }
}
