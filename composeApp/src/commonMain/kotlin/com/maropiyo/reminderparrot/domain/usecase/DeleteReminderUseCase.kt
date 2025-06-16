package com.maropiyo.reminderparrot.domain.usecase

import com.maropiyo.reminderparrot.domain.repository.ReminderRepository

/**
 * リマインダーを削除するユースケース
 *
 * @property reminderRepository リマインダーリポジトリ
 */
class DeleteReminderUseCase(
    private val reminderRepository: ReminderRepository
) {
    /**
     * リマインダーを削除する
     *
     * @param reminderId 削除するリマインダーのID
     * @return 削除結果
     */
    suspend operator fun invoke(reminderId: String): Result<Unit> = reminderRepository.deleteReminder(reminderId)
}
