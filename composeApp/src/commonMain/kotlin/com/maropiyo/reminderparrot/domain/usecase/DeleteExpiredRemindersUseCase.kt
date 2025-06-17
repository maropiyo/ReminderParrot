package com.maropiyo.reminderparrot.domain.usecase

import com.maropiyo.reminderparrot.domain.repository.ReminderRepository
import kotlinx.datetime.Clock

/**
 * 期限切れリマインダー削除UseCase
 *
 * インコが忘れる時間を経過したリマインダーを自動削除する
 */
class DeleteExpiredRemindersUseCase(
    private val reminderRepository: ReminderRepository
) {
    /**
     * 期限切れリマインダーを削除する
     *
     * @return 削除されたリマインダー数
     */
    suspend fun execute(): Result<Int> {
        return try {
            val currentTime = Clock.System.now()
            val deletedCount = reminderRepository.deleteExpiredReminders(currentTime)
            Result.success(deletedCount)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
