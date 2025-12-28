package com.example.socialnet.data.repository

import android.graphics.Color
import com.example.socialnet.data.api.ApiService
import com.example.socialnet.data.db.MessageDao
import com.example.socialnet.data.db.MessageEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.io.IOException
import kotlin.random.Random

class MessageRepository(
    private val apiService: ApiService,
    private val messageDao: MessageDao
) {

    suspend fun getMessages(): List<MessageEntity> = withContext(Dispatchers.IO) {
        try {
            // Fetch posts and users concurrently
            val postsDeferred = async { apiService.getPosts() }
            val usersDeferred = async { apiService.getUsers() }

            val remotePosts = postsDeferred.await()
            val remoteUsers = usersDeferred.await()

            // Create a map of userId to User
            val userMap = remoteUsers.associateBy { it.id }

            // Fetch existing local messages to preserve 'isLiked'
            val localMessages = messageDao.getAllMessages().associateBy { it.id }

            val entities = remotePosts.map { post ->
                val user = userMap[post.userId]
                val existingMessage = localMessages[post.id]

                MessageEntity(
                    id = post.id,
                    userId = post.userId,
                    body = post.body,
                    userName = user?.name ?: "Unknown User",
                    userAvatarColor = existingMessage?.userAvatarColor ?: generateRandomColor(post.userId),
                    isLiked = existingMessage?.isLiked ?: false
                )
            }

            if (entities.isNotEmpty()) {
                // We use insertAll (OnConflictStrategy.REPLACE) instead of updateMessages (deleteAll+insertAll)
                // because updateMessages calls deleteAll() which is redundant since we are replacing all anyway,
                // but explicit insertAll is safer if we want to rely on the entities we just built.
                // However, the original code had 'updateMessages' which wiped everything.
                // Since we are fetching ALL posts, wiping and re-inserting is fine as long as we preserved
                // the local state (isLiked) in the new entities, which we did above.
                messageDao.updateMessages(entities)
            }

            return@withContext entities
        } catch (e: Exception) {
            val localData = messageDao.getAllMessages()
            if (localData.isEmpty()) {
                if (e is IOException) {
                    return@withContext localData
                }
                throw e
            }
            return@withContext localData
        }
    }

    suspend fun toggleLike(id: Int, currentStatus: Boolean) {
        messageDao.updateLikeStatus(id, !currentStatus)
    }

    private fun generateRandomColor(seed: Int): Int {
        val rnd = Random(seed)
        return Color.rgb(rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
    }
}
