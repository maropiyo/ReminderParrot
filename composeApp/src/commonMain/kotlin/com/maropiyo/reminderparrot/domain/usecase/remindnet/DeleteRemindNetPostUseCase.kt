package com.maropiyo.reminderparrot.domain.usecase.remindnet

import com.maropiyo.reminderparrot.domain.repository.RemindNetRepository

/**
 * リマインネット投稿を削除するユースケース
 */
class DeleteRemindNetPostUseCase(
    private val remindNetRepository: RemindNetRepository
) {
    /**
     * 投稿を削除する
     * @param postId 削除する投稿のID
     * @param userId 削除を実行するユーザーのID
     * @return 削除結果
     */
    suspend operator fun invoke(postId: String, userId: String): Result<Unit> {
        return remindNetRepository.deletePost(postId, userId)
    }
}
