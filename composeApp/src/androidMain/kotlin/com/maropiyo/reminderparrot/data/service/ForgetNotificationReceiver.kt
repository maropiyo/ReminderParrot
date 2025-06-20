package com.maropiyo.reminderparrot.data.service

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.maropiyo.reminderparrot.MainActivity

/**
 * リマインダー忘却通知を受信するBroadcastReceiver
 */
class ForgetNotificationReceiver : BroadcastReceiver() {

    companion object {
        private const val CHANNEL_ID = "reminder_forget_channel"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val reminderId = intent.getStringExtra("reminder_id") ?: return
        val reminderText = intent.getStringExtra("reminder_text") ?: "リマインダー"

        // 通知権限の確認
        if (!NotificationManagerCompat.from(context).areNotificationsEnabled()) {
            return
        }

        // メインアクティビティを開くIntent
        val mainIntent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            mainIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        // 申し訳なさそうなメッセージをランダムに選択
        val messages = listOf(
            "ごめん、わすれちゃった...",
            "あ...もうおぼえてない",
            "わすれちゃってごめんね",
            "きえちゃった...ごめん"
        )
        val randomMessage = messages.random()

        // 通知を作成
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // 適切なアイコンに変更
            .setContentTitle("「$reminderText」をわすれちゃった！")
            .setContentText(randomMessage)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        // 通知を表示（Android 13以降の権限チェック付き）
        val notificationManager = NotificationManagerCompat.from(context)
        val notificationId = 10000 + reminderId.hashCode()

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    notificationManager.notify(notificationId, notification)
                }
            } else {
                notificationManager.notify(notificationId, notification)
            }
        } catch (e: SecurityException) {
            // 権限エラーをログに記録（本番環境ではログシステムを使用）
            e.printStackTrace()
        }
    }
}
