package com.maropiyo.reminderparrot.data.service

import com.maropiyo.reminderparrot.domain.entity.Reminder
import com.maropiyo.reminderparrot.domain.service.NotificationService
import kotlin.coroutines.resume
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.datetime.Clock
import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNAuthorizationOptionBadge
import platform.UserNotifications.UNAuthorizationOptionSound
import platform.UserNotifications.UNAuthorizationStatusAuthorized
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNTimeIntervalNotificationTrigger
import platform.UserNotifications.UNUserNotificationCenter

/**
 * iOS固有の通知サービス実装
 */
@OptIn(ExperimentalForeignApi::class)
class IOSNotificationService : NotificationService {

    private val notificationCenter = UNUserNotificationCenter.currentNotificationCenter()

    override suspend fun requestNotificationPermission(): Boolean {
        return suspendCancellableCoroutine { continuation ->
            val options = UNAuthorizationOptionAlert or
                UNAuthorizationOptionSound or
                UNAuthorizationOptionBadge

            notificationCenter.requestAuthorizationWithOptions(
                options = options
            ) { granted, error ->
                continuation.resume(granted && error == null)
            }
        }
    }

    override suspend fun isNotificationPermissionGranted(): Boolean {
        return suspendCancellableCoroutine { continuation ->
            notificationCenter.getNotificationSettingsWithCompletionHandler { settings ->
                val granted = settings?.authorizationStatus == UNAuthorizationStatusAuthorized
                continuation.resume(granted)
            }
        }
    }

    override suspend fun scheduleForgetNotification(reminder: Reminder) {
        // デバッグ用ログ
        println("IOSNotificationService: scheduleForgetNotification called")
        println("  reminder.id: ${reminder.id}")
        println("  reminder.text: ${reminder.text}")
        println("  reminder.forgetAt: ${reminder.forgetAt}")

        if (!isNotificationPermissionGranted()) {
            println("  notification permission not granted")
            throw IllegalStateException("通知権限が許可されていません")
        }

        val currentTime = Clock.System.now().toEpochMilliseconds()
        val forgetTime = reminder.forgetAt.toEpochMilliseconds()
        val timeInterval = (forgetTime - currentTime) / 1000.0

        println("  currentTime: $currentTime")
        println("  forgetTime: $forgetTime")
        println("  timeInterval: $timeInterval seconds")

        // 既に過去の時刻の場合は即座に通知
        if (timeInterval <= 0) {
            println("  scheduling immediate notification (past time)")
            showImmediateNotification(reminder)
            return
        }

        // 申し訳なさそうなメッセージをランダムに選択
        val messages = listOf(
            "ごめん、わすれちゃった...",
            "あ...もうおぼえてない",
            "わすれちゃってごめんね",
            "きえちゃった...ごめん"
        )
        val randomMessage = messages.random()

        // 通知コンテンツを作成
        val content = UNMutableNotificationContent().apply {
            setTitle("「${reminder.text}」をわすれちゃった！")
            setBody(randomMessage)
            setSound(null) // デフォルトサウンド
        }

        // トリガーを作成（指定秒後に通知）
        val trigger = UNTimeIntervalNotificationTrigger.triggerWithTimeInterval(
            timeInterval = timeInterval,
            repeats = false
        )

        // 通知リクエストを作成
        val request = UNNotificationRequest.requestWithIdentifier(
            identifier = "forget_${reminder.id}",
            content = content,
            trigger = trigger
        )

        // 通知をスケジュール
        suspendCancellableCoroutine { continuation ->
            notificationCenter.addNotificationRequest(request) { error ->
                if (error != null) {
                    println("  notification scheduling failed: $error")
                    continuation.resume(Unit) // エラーでも継続
                } else {
                    println("  notification scheduled successfully")
                    continuation.resume(Unit)
                }
            }
        }
    }

    override suspend fun cancelForgetNotification(reminderId: String) {
        val identifiers = listOf("forget_$reminderId")
        notificationCenter.removePendingNotificationRequestsWithIdentifiers(identifiers)
        notificationCenter.removeDeliveredNotificationsWithIdentifiers(identifiers)
    }

    override suspend fun cancelAllForgetNotifications() {
        // すべての忘却通知をキャンセル
        suspendCancellableCoroutine { continuation ->
            notificationCenter.getPendingNotificationRequestsWithCompletionHandler { requests ->
                val forgetIdentifiers = requests?.mapNotNull { request ->
                    val requestId = (request as platform.UserNotifications.UNNotificationRequest).identifier()
                    if (requestId.startsWith("forget_")) requestId else null
                } ?: emptyList()

                if (forgetIdentifiers.isNotEmpty()) {
                    notificationCenter.removePendingNotificationRequestsWithIdentifiers(forgetIdentifiers)
                    notificationCenter.removeDeliveredNotificationsWithIdentifiers(forgetIdentifiers)
                }
                continuation.resume(Unit)
            }
        }
    }

    /**
     * 即座に通知を表示（過去の時刻の場合）
     */
    private suspend fun showImmediateNotification(reminder: Reminder) {
        // 申し訳なさそうなメッセージをランダムに選択
        val messages = listOf(
            "ごめん、わすれちゃった...",
            "あ...もうおぼえてない",
            "わすれちゃってごめんね",
            "きえちゃった...ごめん"
        )
        val randomMessage = messages.random()

        val content = UNMutableNotificationContent().apply {
            setTitle("「${reminder.text}」をわすれちゃった！")
            setBody(randomMessage)
            setSound(null)
        }

        // 即座に発火するトリガー（1秒後）
        val trigger = UNTimeIntervalNotificationTrigger.triggerWithTimeInterval(
            timeInterval = 1.0,
            repeats = false
        )

        val request = UNNotificationRequest.requestWithIdentifier(
            identifier = "forget_immediate_${reminder.id}",
            content = content,
            trigger = trigger
        )

        suspendCancellableCoroutine { continuation ->
            notificationCenter.addNotificationRequest(request) { error ->
                continuation.resume(Unit)
            }
        }
    }

    override suspend fun getPushNotificationToken(): String? {
        return try {
            // FirebaseManagerBridgeを使用してトークンを取得
            FirebaseManagerBridge.getFCMToken()
        } catch (e: Exception) {
            println("iOS: FCMトークン取得エラー: ${e.message}")
            null
        }
    }

    override suspend fun refreshPushNotificationToken(): String? {
        return try {
            // FirebaseManagerBridgeを使用してトークンをリフレッシュ
            FirebaseManagerBridge.refreshFCMToken()
        } catch (e: Exception) {
            println("iOS: FCMトークンリフレッシュエラー: ${e.message}")
            null
        }
    }
}
