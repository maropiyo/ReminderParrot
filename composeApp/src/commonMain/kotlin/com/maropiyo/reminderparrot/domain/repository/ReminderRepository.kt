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

    /**
     * リマインダーを更新する
     *
     * @param reminder 更新するリマインダー
     * @return 更新結果
     */
    suspend fun updateReminder(reminder: Reminder): Result<Unit>

    /**
     * リマインダーを削除する
     *
     * @param reminderId 削除するリマインダーのID
     * @return 削除結果
     */
    suspend fun deleteReminder(reminderId: String): Result<Unit>
}
