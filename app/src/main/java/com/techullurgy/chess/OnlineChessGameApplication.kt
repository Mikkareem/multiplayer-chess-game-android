package com.techullurgy.chess

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.content.getSystemService
import androidx.core.net.toUri
import com.techullurgy.chess.di.initKoin
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext


class OnlineChessGameApplication: Application() {

    val applicationScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onCreate() {
        super.onCreate()

        registerNotificationChannel()

        initKoin {
            androidContext(this@OnlineChessGameApplication)
        }
    }

    private fun registerNotificationChannel() {
        val channel = NotificationChannel("channel_id", "channel_name", NotificationManager.IMPORTANCE_DEFAULT)
        val notificationManager = getSystemService<NotificationManager>()!!
        notificationManager.createNotificationChannel(channel)

        val roomId = 829

        val deepLinkIntent = Intent(this, MainActivity::class.java).apply {
            data = "http://reach-us.com/room/$roomId".toUri()
            action = Intent.ACTION_VIEW
        }

        val pendingIntent = TaskStackBuilder.create(this).run {
            addNextIntentWithParentStack(deepLinkIntent)
            getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE)
        }

        val notification = NotificationCompat.Builder(this, channel.id)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Deeplink Test")
            .setContentText("This will open the screen f Game Room with room id : $roomId")
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(1, notification)
    }
}