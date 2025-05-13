package com.maropiyo.reminderparrot.domain.repository

import com.maropiyo.reminderparrot.domain.entity.Reminder

/**
 * リマインダーリポジトリ
 */
interface ReminderRepository {
    /**
     * リマインダーを作成する
     *
     * @param reminder リマインダー
     * @return 作成したリマインダー
     */
    suspend fun createReminder(reminder: Reminder): Result<Reminder>

    /**
     * リマインダーを取得する
     *
     * @return リマインダーのリスト
     */
    suspend fun getReminders(): Result<List<Reminder>>
}
