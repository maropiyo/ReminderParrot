package com.maropiyo.reminderparrot.domain.usecase.remindnet

import com.maropiyo.reminderparrot.domain.entity.RemindNetPost
import com.maropiyo.reminderparrot.domain.repository.RemindNetRepository
import kotlinx.datetime.Instant

/**
 * リマインネットに投稿を作成するユースケース
 */
class CreateRemindNetPostUseCase(
    private val remindNetRepository: RemindNetRepository
) {
    suspend operator fun invoke(
        reminderText: String,
        forgetAt: Instant,
        userId: String? = null,
        userName: String? = null
    ): Result<RemindNetPost> {
        return remindNetRepository.createPost(reminderText, forgetAt, userId, userName)
    }
}
