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

import kotlinx.coroutines.flow.Flow

class MessageRepository(
    private val apiService: ApiService,
    private val messageDao: MessageDao
) {

    val messages: Flow<List<MessageEntity>> = messageDao.getAllMessages()

    suspend fun refreshMessages() = withContext(Dispatchers.IO) {
        try {
            // Fetch posts and users concurrently
            val postsDeferred = async { apiService.getPosts() }
            val usersDeferred = async { apiService.getUsers() }

            val remotePosts = postsDeferred.await()
            val remoteUsers = usersDeferred.await()

            // Create a map of userId to User
            val userMap = remoteUsers.associateBy { it.id }

            // Fetch existing local messages
            val localMessagesList = messageDao.getAllMessagesOneShot()
            val localMessagesMap = localMessagesList.associateBy { it.id }

            val entities = if (localMessagesList.isEmpty()) {
                // Initial load: pick 10 random posts
                android.util.Log.d("MessageRepository", "Local DB empty. Picking 10 random posts.")
                remotePosts.shuffled().take(10).map { post ->
                    val user = userMap[post.userId]
                    MessageEntity(
                        id = post.id,
                        userId = post.userId,
                        body = post.body,
                        userName = user?.name ?: "Unknown User",
                        userAvatarColor = generateRandomColor(post.userId),
                        isLiked = false,
                        timestamp = System.currentTimeMillis()
                    )
                }
            } else {
                // Refresh/Update: pick 1-2 random posts
                val countToAdd = Random.nextInt(1, 3) // 1 or 2
                android.util.Log.d("MessageRepository", "Refreshing. Adding $countToAdd random posts.")

                // Filter out posts that are already in local DB
                val availablePosts = remotePosts.filter { !localMessagesMap.containsKey(it.id) }

                val newPosts = if (availablePosts.isNotEmpty()) {
                    availablePosts.shuffled().take(countToAdd)
                } else {
                    // Fallback if we have all posts (unlikely with 100 limit but good to handle)
                    emptyList()
                }

                val newEntities = newPosts.map { post ->
                    val user = userMap[post.userId]
                    MessageEntity(
                        id = post.id,
                        userId = post.userId,
                        body = post.body,
                        userName = user?.name ?: "Unknown User",
                        userAvatarColor = generateRandomColor(post.userId),
                        isLiked = false,
                        timestamp = System.currentTimeMillis()
                    )
                }

                // We combine for insertion.
                localMessagesList + newEntities
            }

            if (entities.isNotEmpty()) {
                messageDao.insertAll(entities)
            }

        } catch (e: Exception) {
            android.util.Log.e("MessageRepository", "Error fetching messages", e)
            // No return needed as we rely on Flow
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
