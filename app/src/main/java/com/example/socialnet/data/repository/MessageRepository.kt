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
            // In case of error, return data from DB
            val localData = messageDao.getAllMessages()
            if (localData.isEmpty()) {
                // If DB is also empty, re-throw exception or return empty list?
                // For now, let's re-throw so UI can show error if absolutely nothing is available
                // Or maybe just return empty. The requirement says:
                // "При отсутствии сети - загружать сообщения из базы"
                if (e is IOException) {
                    return localData // Return local even if empty if it's network error
                }
                throw e
            }
            return localData
        }
    }
}
