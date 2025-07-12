package com.maropiyo.reminderparrot.data.repository

import com.maropiyo.reminderparrot.data.datasource.local.ReminderLocalDataSource
import com.maropiyo.reminderparrot.domain.entity.Reminder
import com.maropiyo.reminderparrot.domain.repository.ReminderRepository
import kotlinx.datetime.Instant

/**
 * リマインダーリポジトリの実装
 *
 * @param localDataSource ローカルデータソース
 */
class ReminderRepositoryImpl(
    private val localDataSource: ReminderLocalDataSource
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

    /**
     * リマインダーを更新する
     *
     * @param reminder 更新するリマインダー
     * @return 更新結果
     * @throws Exception リマインダーの更新に失敗した場合
     */
    override suspend fun updateReminder(reminder: Reminder): Result<Unit> = try {
        localDataSource.updateReminder(reminder)
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    /**
     * リマインダーを削除する
     *
     * @param reminderId 削除するリマインダーのID
     * @return 削除結果
     * @throws Exception リマインダーの削除に失敗した場合
     */
    override suspend fun deleteReminder(reminderId: String): Result<Unit> = try {
        localDataSource.deleteReminder(reminderId)
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    /**
     * 期限切れリマインダーを削除する
     *
     * @param currentTime 現在時刻
     * @return 削除されたリマインダー数
     */
    override suspend fun deleteExpiredReminders(currentTime: Instant): Int {
        return localDataSource.deleteExpiredReminders(currentTime)
    }
}
