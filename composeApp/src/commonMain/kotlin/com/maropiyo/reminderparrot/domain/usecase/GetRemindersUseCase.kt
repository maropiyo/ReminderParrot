package com.maropiyo.reminderparrot.domain.usecase

import com.maropiyo.reminderparrot.domain.entity.Reminder
import com.maropiyo.reminderparrot.domain.repository.ReminderRepository

/**
 * リマインダー取得ユースケース
 *
 * @property reminderRepository リマインダーリポジトリ
 */
class GetRemindersUseCase(
    private val reminderRepository: ReminderRepository
) {
    /**
     * リマインダーを取得する
     *
     * @return リマインダーのリスト
     */
    suspend operator fun invoke(): Result<List<Reminder>> = reminderRepository.getReminders()
}
