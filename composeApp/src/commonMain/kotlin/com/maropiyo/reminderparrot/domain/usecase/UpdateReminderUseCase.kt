package com.maropiyo.reminderparrot.domain.usecase

import com.maropiyo.reminderparrot.domain.entity.Reminder
import com.maropiyo.reminderparrot.domain.repository.ReminderRepository

/**
 * リマインダーを更新するユースケース
 *
 * @property reminderRepository リマインダーリポジトリ
 */
class UpdateReminderUseCase(
    private val reminderRepository: ReminderRepository
) {
    /**
     * リマインダーを更新する
     *
     * @param reminder 更新するリマインダー
     * @return 更新結果
     */
    suspend operator fun invoke(reminder: Reminder): Result<Unit> = reminderRepository.updateReminder(reminder)
}
