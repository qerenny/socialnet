package com.example.socialnet.data.api

import com.example.socialnet.data.model.Post
import retrofit2.http.GET

interface ApiService {
    @GET("posts")
    suspend fun getPosts(): List<Post>
}
