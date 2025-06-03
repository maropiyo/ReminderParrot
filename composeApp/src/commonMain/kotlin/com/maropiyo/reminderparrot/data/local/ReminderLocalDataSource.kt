package com.maropiyo.reminderparrot.data.local

import com.maropiyo.reminderparrot.data.mapper.ReminderMapper
import com.maropiyo.reminderparrot.db.ReminderParrotDatabase
import com.maropiyo.reminderparrot.domain.entity.Reminder

/**
 * リマインダーのローカルデータソース
 *
 * @property database データベース
 * @property reminderMapper リマインダーマッパー
 */
class ReminderLocalDataSource(
    private val database: ReminderParrotDatabase,
    private val reminderMapper: ReminderMapper
) {
    /**
     * リマインダーを作成する
     *
     * @param reminder リマインダー
     * @return 作成したリマインダー
     */
    fun createReminder(reminder: Reminder): Reminder {
        database.reminderParrotDatabaseQueries.insertReminder(
            id = reminder.id,
            text = reminder.text,
            is_completed = if (reminder.isCompleted) 1L else 0L
        )
        return reminder
    }

    /**
     * リマインダーを取得する
     */
    fun getReminders(): List<Reminder> =
        database.reminderParrotDatabaseQueries.selectAllReminders(reminderMapper::mapFromDatabase).executeAsList()

    /**
     * リマインダーを更新する
     *
     * @param reminder リマインダー
     */
    fun updateReminder(reminder: Reminder) {
        database.reminderParrotDatabaseQueries.updateReminder(
            text = reminder.text,
            is_completed = if (reminder.isCompleted) 1L else 0L,
            id = reminder.id
        )
    }
}
