package com.maropiyo.reminderparrot.domain.usecase

import com.maropiyo.reminderparrot.domain.service.NotificationService

/**
 * 通知権限要求ユースケース
 *
 * @property notificationService 通知サービス
 */
class RequestNotificationPermissionUseCase(
    private val notificationService: NotificationService
) {
    /**
     * 通知権限を要求する
     *
     * @return 権限が許可された場合true、拒否された場合false
     */
    suspend operator fun invoke(): Boolean {
        return try {
            notificationService.requestNotificationPermission()
        } catch (e: Exception) {
            false
        }
    }
}
