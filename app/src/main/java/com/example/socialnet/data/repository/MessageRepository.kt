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

            // Fetch existing local messages
            val localMessagesList = messageDao.getAllMessages()
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
                        isLiked = false
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
                        isLiked = false
                    )
                }

                // We return all local messages plus the new ones for the UI
                localMessagesList + newEntities
            }

            if (entities.isNotEmpty()) {
                // Using insertAll with REPLACE strategy allows us to add new ones and keep existing ones
                // But wait, 'entities' contains EVERYTHING (if we are in the else block above, we combined local + new).
                // If we insert all 'entities', we are re-inserting existing ones which is fine (REPLACE).

                // Actually, for the 'else' block (incremental update), we only really need to insert the *new* ones into DB.
                // But 'insertAll' with 'entities' (which is everything) ensures consistency.
                // However, 'localMessagesList' already has correct 'isLiked' state.
                // Re-mapping remotePosts in the initial logic wiped 'isLiked'.
                // Here, I constructed 'entities' carefully:
                // Case 1 (Empty): just 10 new ones.
                // Case 2 (Not Empty): 'localMessagesList' (preserved state) + 'newEntities'.

                // So inserting 'entities' is safe and correct.
                messageDao.insertAll(entities)
            }

            // Return the full list from the DB to be sure
            return@withContext messageDao.getAllMessages()
        } catch (e: Exception) {
            android.util.Log.e("MessageRepository", "Error fetching messages", e)
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
