package com.maropiyo.reminderparrot.data.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.maropiyo.reminderparrot.MainActivity
import com.maropiyo.reminderparrot.R

/**
 * FCMプッシュ通知を受信するサービス
 */
class ReminderParrotMessagingService : FirebaseMessagingService() {

    companion object {
        private const val CHANNEL_ID = "remindnet_notification_channel"
        private const val CHANNEL_NAME = "リマインネット通知"
        private const val CHANNEL_DESCRIPTION = "リマインネットからのリマインド通知"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        println("FCM新しいトークン: $token")
        // TODO: 新しいトークンをSupabaseに送信
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        
        println("FCMメッセージ受信: ${remoteMessage.data}")
        
        // 通知データを取得
        val title = remoteMessage.notification?.title ?: remoteMessage.data["title"] ?: "リマインコ"
        val body = remoteMessage.notification?.body ?: remoteMessage.data["body"] ?: ""
        
        // 通知を表示
        showNotification(title, body)
    }

    /**
     * 通知を表示する
     */
    private fun showNotification(title: String, body: String) {
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
    }

    /**
     * 通知チャンネルを作成
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = CHANNEL_DESCRIPTION
            }

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}