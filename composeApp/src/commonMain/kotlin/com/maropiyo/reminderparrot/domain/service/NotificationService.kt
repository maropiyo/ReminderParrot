package com.maropiyo.reminderparrot.domain.service

import com.maropiyo.reminderparrot.domain.entity.Reminder

/**
 * 通知サービスのインターフェース
 *
 * プラットフォーム固有の通知機能を抽象化したインターフェースです
 */
interface NotificationService {
    /**
     * 通知権限の要求
     *
     * @return 権限が許可された場合true、拒否された場合false
     */
    suspend fun requestNotificationPermission(): Boolean

    /**
     * 通知権限の状態を確認
     *
     * @return 権限が許可されている場合true、拒否されている場合false
     */
    suspend fun isNotificationPermissionGranted(): Boolean

    /**
     * リマインダーの忘却通知をスケジュール
     *
     * @param reminder 通知対象のリマインダー
     */
    suspend fun scheduleForgetNotification(reminder: Reminder)

    /**
     * 指定されたリマインダーの忘却通知をキャンセル
     *
     * @param reminderId キャンセル対象のリマインダーID
     */
    suspend fun cancelForgetNotification(reminderId: String)

    /**
     * すべての忘却通知をキャンセル
     */
    suspend fun cancelAllForgetNotifications()
}
