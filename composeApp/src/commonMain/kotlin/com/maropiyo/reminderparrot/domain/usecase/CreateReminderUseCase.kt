package com.maropiyo.reminderparrot.domain.usecase

import com.maropiyo.reminderparrot.domain.entity.Reminder
import com.maropiyo.reminderparrot.domain.repository.ReminderRepository

/**
 * リマインダー作成ユースケース
 *
 * @property reminderRepository リマインダーリポジトリ
 */
class CreateReminderUseCase(
    private val reminderRepository: ReminderRepository
) {
    /**
     * リマインダーを作成する
     *
     * @param id リマインダーID
     * @param text リマインダーテキスト
     * @return 作成したリマインダー
     */
    suspend operator fun invoke(
        id: String,
        text: String
    ): Result<Reminder> {
        val reminder =
            Reminder(
                id = id,
                text = text,
            )

        return reminderRepository.createReminder(reminder)
    }
}
