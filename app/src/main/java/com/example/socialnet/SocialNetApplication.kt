package com.example.socialnet

import android.app.Application
import androidx.room.Room
import com.example.socialnet.data.api.ApiService
import com.example.socialnet.data.db.AppDatabase
import com.example.socialnet.data.repository.MessageRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SocialNetApplication : Application() {

    private val database by lazy {
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "socialnet_db"
        ).build()
    }

    private val apiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://jsonplaceholder.typicode.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    val repository by lazy {
        MessageRepository(apiService, database.messageDao())
    }
}
