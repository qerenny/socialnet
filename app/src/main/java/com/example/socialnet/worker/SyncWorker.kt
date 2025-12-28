package com.example.socialnet.worker

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.socialnet.R
import com.example.socialnet.SocialNetApplication
import java.util.concurrent.TimeUnit

class SyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        try {
            android.util.Log.d("SyncWorker", "Starting background sync")
            val app = applicationContext as SocialNetApplication
            val repository = app.repository

            repository.refreshMessages()

            showNotification()

            android.util.Log.d("SyncWorker", "Background sync success")
        } catch (e: Exception) {
            android.util.Log.e("SyncWorker", "Background sync failed", e)
            e.printStackTrace()
            // Even if failed, we probably want to schedule next one?
            // Or return failure. For this task, let's keep retrying via scheduleNext.
        } finally {
            scheduleNextWork()
        }
        return Result.success()
    }

    private fun scheduleNextWork() {
        android.util.Log.d("SyncWorker", "Scheduling next sync in 5 seconds")
        val nextRequest = OneTimeWorkRequestBuilder<SyncWorker>()
            .setInitialDelay(5, TimeUnit.SECONDS)
            .build()

        WorkManager.getInstance(applicationContext).enqueueUniqueWork(
            "SyncWorkRecursive",
            ExistingWorkPolicy.REPLACE, // Replace current (which is finishing) with new future one
            nextRequest
        )
    }

    private fun showNotification() {
        android.util.Log.d("SyncWorker", "Attempting to show notification")

        val channelId = "sync_channel"
        val notificationId = 1

        // Create channel first (needed for permission check on O+ sometimes, but definitely before notify)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Sync Channel"
            val descriptionText = "Notifications for data sync"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                android.util.Log.w("SyncWorker", "Missing POST_NOTIFICATIONS permission")
                return
            }
        }

        val builder = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(android.R.drawable.ic_popup_sync)
            .setContentTitle("SocialNet")
            .setContentText("Новые данные получены")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        NotificationManagerCompat.from(applicationContext).notify(notificationId, builder.build())
        android.util.Log.d("SyncWorker", "Notification shown")
    }
}
