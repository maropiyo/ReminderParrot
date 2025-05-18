package com.maropiyo.reminderparrot.domain.usecase

import com.maropiyo.reminderparrot.domain.common.UuidGenerator
import com.maropiyo.reminderparrot.domain.entity.Reminder
import com.maropiyo.reminderparrot.domain.repository.ReminderRepository

/**
 * リマインダー作成ユースケース
 *
 * @property reminderRepository リマインダーリポジトリ
 * @property uuidGenerator UUIDジェネレーター
 */
class CreateReminderUseCase(
    private val reminderRepository: ReminderRepository,
    private val uuidGenerator: UuidGenerator
) {
    /**
     * リマインダーを作成する
     *
     * @param text リマインダーテキスト
     * @return 作成したリマインダー
     */
    suspend operator fun invoke(text: String): Result<Reminder> {
        val reminder =
            Reminder(
                id = uuidGenerator.generateId(),
                text = text
            )

        return reminderRepository.createReminder(reminder)
    }
}
