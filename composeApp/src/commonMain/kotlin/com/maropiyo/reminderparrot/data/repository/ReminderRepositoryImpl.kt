package com.maropiyo.reminderparrot.data.repository

import com.maropiyo.reminderparrot.data.local.ReminderLocalDataSource
import com.maropiyo.reminderparrot.data.remote.ReminderRemoteDataSource
import com.maropiyo.reminderparrot.domain.entity.Reminder
import com.maropiyo.reminderparrot.domain.repository.ReminderRepository

/**
 * リマインダーリポジトリの実装
 *
 * @param localDataSource ローカルデータソース
 * @property remoteDataSource リモートデータソース
 */
class ReminderRepositoryImpl(
    private val localDataSource: ReminderLocalDataSource,
    private val remoteDataSource: ReminderRemoteDataSource
) : ReminderRepository {
    /**
     * リマインダーを作成する
     *
     * @param reminder リマインダー
     * @return 作成したリマインダー
     * @throws Exception リマインダーの取得に失敗した場合
     */
    override suspend fun createReminder(reminder: Reminder): Result<Reminder> = try {
        Result.success(localDataSource.createReminder(reminder))
    } catch (e: Exception) {
        Result.failure(e)
    }

    /**
     * リマインダーを取得する
     *
     * @return リマインダーのリスト
     * @throws Exception リマインダーの取得に失敗した場合
     */
    override suspend fun getReminders(): Result<List<Reminder>> = try {
        Result.success(localDataSource.getReminders())
    } catch (e: Exception) {
        Result.failure(e)
    }
}
