package com.maropiyo.reminderparrot.domain.usecase

import com.maropiyo.reminderparrot.domain.entity.Reminder
import com.maropiyo.reminderparrot.domain.service.NotificationService

/**
 * リマインダー忘却通知のスケジューリングユースケース
 *
 * @property notificationService 通知サービス
 */
class ScheduleForgetNotificationUseCase(
    private val notificationService: NotificationService
) {
    /**
     * リマインダーの忘却通知をスケジュールする
     *
     * @param reminder 通知対象のリマインダー
     * @return 成功した場合はSuccess、失敗した場合はFailure
     */
    suspend operator fun invoke(reminder: Reminder): Result<Unit> {
        return try {
            // 通知権限の確認
            val hasPermission = notificationService.isNotificationPermissionGranted()

            if (!hasPermission) {
                val permissionGranted = notificationService.requestNotificationPermission()
                if (!permissionGranted) {
                    return Result.failure(
                        IllegalStateException("通知権限が許可されていません")
                    )
                }
            }

            // 通知のスケジュール
            notificationService.scheduleForgetNotification(reminder)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
