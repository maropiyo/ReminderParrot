package com.maropiyo.reminderparrot.domain.usecase.remindnet

import com.maropiyo.reminderparrot.domain.entity.RemindNetPost
import com.maropiyo.reminderparrot.domain.repository.ParrotRepository
import com.maropiyo.reminderparrot.domain.repository.RemindNetRepository
import com.maropiyo.reminderparrot.domain.service.AuthService
import kotlinx.datetime.Instant

/**
 * リマインネットに投稿を作成するユースケース
 */
class CreateRemindNetPostUseCase(
    private val remindNetRepository: RemindNetRepository,
    private val authService: AuthService,
    private val parrotRepository: ParrotRepository
) {
    suspend operator fun invoke(
        reminderId: String,
        reminderText: String,
        forgetAt: Instant,
        userName: String? = null
    ): Result<RemindNetPost> {
        // SupabaseAuthで匿名認証ユーザーIDを取得
        val userId = authService.getUserId()
        // パラメータで指定されていない場合はSupabaseAuthから取得
        val actualUserName = userName ?: authService.getDisplayName()

        // ローカルのインコからレベル情報を取得
        val userLevel = parrotRepository.getParrot().getOrNull()?.level

        return remindNetRepository.createPost(reminderId, reminderText, forgetAt, userId, actualUserName, userLevel)
    }
}
