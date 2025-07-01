package com.maropiyo.reminderparrot.domain.usecase.remindnet

import com.maropiyo.reminderparrot.domain.entity.RemindNetPost
import com.maropiyo.reminderparrot.domain.repository.RemindNetRepository
import kotlinx.coroutines.flow.Flow

/**
 * リマインネットの投稿を取得するユースケース
 */
class GetRemindNetPostsUseCase(
    private val remindNetRepository: RemindNetRepository
) {
    operator fun invoke(): Flow<List<RemindNetPost>> {
        return remindNetRepository.getAllPosts()
    }
}
