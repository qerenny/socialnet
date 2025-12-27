package com.example.socialnet.data.api

import com.example.socialnet.data.model.Post
import com.example.socialnet.data.model.User
import retrofit2.http.GET

interface ApiService {
    @GET("posts")
    suspend fun getPosts(): List<Post>

    @GET("users")
    suspend fun getUsers(): List<User>
}
