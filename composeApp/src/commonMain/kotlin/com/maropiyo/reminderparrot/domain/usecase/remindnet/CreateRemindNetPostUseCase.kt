package com.maropiyo.reminderparrot.domain.usecase.remindnet

import com.maropiyo.reminderparrot.domain.entity.RemindNetPost
import com.maropiyo.reminderparrot.domain.repository.RemindNetRepository
import com.maropiyo.reminderparrot.domain.service.UserIdService
import kotlinx.datetime.Instant

/**
 * リマインネットに投稿を作成するユースケース
 */
class CreateRemindNetPostUseCase(
    private val remindNetRepository: RemindNetRepository,
    private val userIdService: UserIdService
) {
    suspend operator fun invoke(
        reminderId: String,
        reminderText: String,
        forgetAt: Instant,
        userName: String? = null
    ): Result<RemindNetPost> {
        // デバイス固有のユーザーIDを自動取得
        val userId = userIdService.getUserId()
        
        return remindNetRepository.createPost(reminderId, reminderText, forgetAt, userId, userName)
    }
}
