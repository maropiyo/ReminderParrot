package com.maropiyo.reminderparrot.data.service

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
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

        // 通知を作成
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // 適切なアイコンに変更
            .setContentTitle("「$reminderText」をわすれちゃった！")
            .setContentText("ぴよぴよ〜！もうおぼえてないや〜")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        // 通知を表示
        val notificationManager = NotificationManagerCompat.from(context)
        val notificationId = 10000 + reminderId.hashCode()
        notificationManager.notify(notificationId, notification)
    }
}
