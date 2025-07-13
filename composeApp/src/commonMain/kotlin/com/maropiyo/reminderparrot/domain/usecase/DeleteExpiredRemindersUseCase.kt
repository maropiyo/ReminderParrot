package com.maropiyo.reminderparrot.domain.usecase

import com.maropiyo.reminderparrot.domain.repository.ReminderRepository
import com.maropiyo.reminderparrot.domain.service.AuthService
import com.maropiyo.reminderparrot.domain.usecase.remindnet.DeleteRemindNetPostUseCase
import kotlinx.datetime.Clock

/**
 * 期限切れリマインダー削除UseCase
 *
 * インコが忘れる時間を経過したリマインダーを自動削除する
 * 対応するリマインネット投稿も連動して削除する
 */
class DeleteExpiredRemindersUseCase(
    private val reminderRepository: ReminderRepository,
    private val deleteRemindNetPostUseCase: DeleteRemindNetPostUseCase,
    private val authService: AuthService
) {
    /**
     * 期限切れリマインダーを削除する
     * 対応するリマインネット投稿も連動削除する
     *
     * @return 削除されたリマインダー数
     */
    suspend fun execute(): Result<Int> {
        return try {
            val currentTime = Clock.System.now()

            // 削除対象のリマインダーIDを事前に取得
            val expiredReminderIds = reminderRepository.getExpiredReminderIds(currentTime)

            // リマインダーを削除
            val deletedCount = reminderRepository.deleteExpiredReminders(currentTime)

            // 対応するリマインネット投稿も削除
            deleteRemindNetPostsIfExists(expiredReminderIds)

            Result.success(deletedCount)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * リマインネット投稿が存在する場合に削除する
     * エラーが発生しても処理は継続する
     *
     * @param reminderIds 削除されたリマインダーのIDリスト
     */
    private suspend fun deleteRemindNetPostsIfExists(reminderIds: List<String>) {
        try {
            val currentUserId = authService.getCurrentUserId()
            if (currentUserId != null) {
                reminderIds.forEach { reminderId ->
                    deleteRemindNetPostUseCase(reminderId, currentUserId)
                        .onFailure { // エラーが発生してもログのみ出力し、処理は継続
                            println("期限切れリマインダー連動削除でRemindNet投稿削除に失敗: ${it.message}")
                        }
                }
            }
        } catch (e: Exception) {
            // 例外が発生してもリマインダー削除処理は継続
            println("期限切れリマインダー連動削除でエラー: ${e.message}")
        }
    }
}
