package com.maropiyo.reminderparrot.data.datasource.local

import com.maropiyo.reminderparrot.data.mapper.ReminderMapper
import com.maropiyo.reminderparrot.db.ReminderParrotDatabase
import com.maropiyo.reminderparrot.domain.entity.Reminder
import kotlinx.datetime.Instant

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
            is_completed = if (reminder.isCompleted) 1L else 0L,
            created_at = reminder.createdAt.toEpochMilliseconds(),
            forget_at = reminder.forgetAt.toEpochMilliseconds()
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

    /**
     * リマインダーを削除する
     *
     * @param reminderId 削除するリマインダーのID
     */
    fun deleteReminder(reminderId: String) {
        database.reminderParrotDatabaseQueries.deleteReminder(reminderId)
    }

    /**
     * 期限切れリマインダーを削除する
     *
     * @param currentTime 現在時刻
     * @return 削除されたリマインダー数
     */
    fun deleteExpiredReminders(currentTime: Instant): Int {
        // 削除前のカウントを取得
        val beforeCount = database.reminderParrotDatabaseQueries.selectAllReminders().executeAsList().size

        // 期限切れリマインダーを削除
        database.reminderParrotDatabaseQueries.deleteExpiredReminders(currentTime.toEpochMilliseconds())

        // 削除後のカウントを取得して差分を計算
        val afterCount = database.reminderParrotDatabaseQueries.selectAllReminders().executeAsList().size

        return beforeCount - afterCount
    }
}
