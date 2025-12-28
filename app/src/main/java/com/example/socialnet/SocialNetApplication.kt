package com.example.socialnet

import android.app.Application
import androidx.room.Room
import com.example.socialnet.data.api.ApiService
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.socialnet.data.db.AppDatabase
import com.example.socialnet.data.repository.MessageRepository
import com.example.socialnet.worker.SyncWorker
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class SocialNetApplication : Application() {

    private val database by lazy {
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "socialnet_db"
        )
        .fallbackToDestructiveMigration()
        .build()
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

    override fun onCreate() {
        super.onCreate()
        setupWorker()
    }

    private fun setupWorker() {
        // Enqueue the first one-time request immediately (or with small delay)
        // Subsequent requests will be chained by the worker itself
        val syncRequest = OneTimeWorkRequestBuilder<SyncWorker>()
            .build()

        WorkManager.getInstance(this).enqueueUniqueWork(
            "SyncWorkRecursive",
            ExistingWorkPolicy.KEEP, // If already running/enqueued, don't replace
            syncRequest
        )
    }
}
