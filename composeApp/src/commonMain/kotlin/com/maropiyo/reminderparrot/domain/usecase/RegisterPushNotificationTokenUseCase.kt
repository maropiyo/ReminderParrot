package com.maropiyo.reminderparrot.domain.usecase

import com.maropiyo.reminderparrot.domain.entity.Platform
import com.maropiyo.reminderparrot.domain.repository.RemindNetRepository
import com.maropiyo.reminderparrot.domain.service.AuthService
import com.maropiyo.reminderparrot.domain.service.NotificationService
import com.maropiyo.reminderparrot.util.PlatformUtil

/**
 * プッシュ通知トークンを登録するUseCase
 *
 * デバイストークンを取得してSupabaseに保存する
 */
class RegisterPushNotificationTokenUseCase(
    private val notificationService: NotificationService,
    private val remindNetRepository: RemindNetRepository,
    private val authService: AuthService,
    private val platformUtil: PlatformUtil
) {
    suspend operator fun invoke(): Result<Unit> {
        try {
            // ユーザーIDを取得
            val userId = authService.getCurrentUserId()
                ?: return Result.failure(Exception("ユーザーがログインしていません"))

            // プッシュ通知トークンを取得
            val token = notificationService.getPushNotificationToken()
                ?: return Result.failure(Exception("プッシュ通知トークンを取得できませんでした"))

            // プラットフォームを判定
            val platform = when {
                platformUtil.isAndroid() -> Platform.ANDROID
                platformUtil.isIOS() -> Platform.IOS
                else -> return Result.failure(Exception("サポートされていないプラットフォームです"))
            }

            // トークンをSupabaseに保存
            return remindNetRepository.registerPushNotificationToken(
                userId = userId,
                token = token,
                platform = platform
            )
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }
}
