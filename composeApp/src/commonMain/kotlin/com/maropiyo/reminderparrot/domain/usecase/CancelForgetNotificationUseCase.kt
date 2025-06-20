package com.maropiyo.reminderparrot.domain.usecase

import com.maropiyo.reminderparrot.domain.service.NotificationService

/**
 * リマインダー忘却通知のキャンセルユースケース
 *
 * @property notificationService 通知サービス
 */
class CancelForgetNotificationUseCase(
    private val notificationService: NotificationService
) {
    /**
     * 指定されたリマインダーの忘却通知をキャンセルする
     *
     * @param reminderId キャンセル対象のリマインダーID
     * @return 成功した場合はSuccess、失敗した場合はFailure
     */
    suspend operator fun invoke(reminderId: String): Result<Unit> {
        return try {
            notificationService.cancelForgetNotification(reminderId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * すべての忘却通知をキャンセルする
     *
     * @return 成功した場合はSuccess、失敗した場合はFailure
     */
    suspend fun cancelAll(): Result<Unit> {
        return try {
            notificationService.cancelAllForgetNotifications()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
