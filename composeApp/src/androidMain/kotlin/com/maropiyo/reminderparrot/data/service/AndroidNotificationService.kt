package com.maropiyo.reminderparrot.data.service

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.maropiyo.reminderparrot.MainActivity
import com.maropiyo.reminderparrot.domain.entity.Reminder
import com.maropiyo.reminderparrot.domain.service.NotificationService
import kotlinx.datetime.Clock

/**
 * Android固有の通知サービス実装
 *
 * @property context Androidアプリケーションコンテキスト
 */
class AndroidNotificationService(
    private val context: Context
) : NotificationService {

    companion object {
        private const val CHANNEL_ID = "reminder_forget_channel"
        private const val CHANNEL_NAME = "リマインダー忘却通知"
        private const val CHANNEL_DESCRIPTION = "リマインダーを忘れた時の通知"
        private const val NOTIFICATION_REQUEST_CODE_BASE = 10000
    }

    init {
        createNotificationChannel()
    }

    override suspend fun requestNotificationPermission(): Boolean {
        // Android 13 (API 33) 以降では実行時権限が必要
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // 実際の権限要求は Activity で行う必要があるため、
            // ここでは現在の権限状態を返す
            isNotificationPermissionGranted()
        } else {
            // Android 12 以下では通知権限は必要ない
            true
        }
    }

    override suspend fun isNotificationPermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            // Android 12 以下では通知権限チェック不要
            NotificationManagerCompat.from(context).areNotificationsEnabled()
        }
    }

    override suspend fun scheduleForgetNotification(reminder: Reminder) {
        if (!isNotificationPermissionGranted()) {
            throw IllegalStateException("通知権限が許可されていません")
        }

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = createNotificationIntent(reminder)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            getRequestCode(reminder.id),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val triggerTime = reminder.forgetAt.toEpochMilliseconds()
        val currentTime = Clock.System.now().toEpochMilliseconds()

        // 既に過去の時刻の場合は即座に通知
        if (triggerTime <= currentTime) {
            showForgetNotification(reminder)
        } else {
            // 指定時刻に通知をスケジュール
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            }
        }
    }

    override suspend fun cancelForgetNotification(reminderId: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ForgetNotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            getRequestCode(reminderId),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )

        pendingIntent?.let {
            alarmManager.cancel(it)
            it.cancel()
        }
    }

    override suspend fun cancelAllForgetNotifications() {
        // 個別のキャンセルは実装が複雑なため、
        // 必要に応じてIDを管理するシステムを追加
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

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * 忘却通知を表示
     */
    private fun showForgetNotification(reminder: Reminder) {
        if (!NotificationManagerCompat.from(context).areNotificationsEnabled()) return

        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // 適切なアイコンに変更
            .setContentTitle("「${reminder.text}」をわすれちゃった！")
            .setContentText("ぴよぴよ〜！もうおぼえてないや〜")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(getRequestCode(reminder.id), notification)
    }

    /**
     * 通知用のIntentを作成
     */
    private fun createNotificationIntent(reminder: Reminder): Intent {
        return Intent(context, ForgetNotificationReceiver::class.java).apply {
            putExtra("reminder_id", reminder.id)
            putExtra("reminder_text", reminder.text)
        }
    }

    /**
     * リマインダーIDに基づいてリクエストコードを生成
     */
    private fun getRequestCode(reminderId: String): Int {
        return NOTIFICATION_REQUEST_CODE_BASE + reminderId.hashCode()
    }
}
