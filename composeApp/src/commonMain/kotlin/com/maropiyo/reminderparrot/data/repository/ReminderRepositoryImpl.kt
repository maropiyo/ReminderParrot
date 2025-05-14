package com.maropiyo.reminderparrot.data.repository

import com.maropiyo.reminderparrot.data.remote.ReminderRemoteDataSource
import com.maropiyo.reminderparrot.domain.entity.Reminder
import com.maropiyo.reminderparrot.domain.repository.ReminderRepository

/**
 * リマインダーリポジトリの実装
 *
 * @property remoteDataSource リモートデータソース
 */
class ReminderRepositoryImpl(
    private val remoteDataSource: ReminderRemoteDataSource
) : ReminderRepository {
    /**
     * リマインダーを作成する
     *
     * @param reminder リマインダー
     * @return 作成したリマインダー
     * @throws Exception リモートデータソースからの取得に失敗した場合
     */
    override suspend fun createReminder(reminder: Reminder): Result<Reminder> =
        remoteDataSource.createReminder(reminder)

    /**
     * リマインダーを取得する
     *
     * @return リマインダーのリスト
     * @throws Exception リモートデータソースからの取得に失敗した場合
     */
    override suspend fun getReminders(): Result<List<Reminder>> = remoteDataSource.getReminders()
}
