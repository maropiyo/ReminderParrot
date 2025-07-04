package com.maropiyo.reminderparrot.data.remote

import com.maropiyo.reminderparrot.data.mapper.ReminderMapper
import com.maropiyo.reminderparrot.data.model.ReminderDto
import com.maropiyo.reminderparrot.domain.entity.Reminder
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from

/**
 * リマインダーのリモートデータソース
 *
 * @property supabaseClient Supabaseクライアント
 * @property reminderMapper リマインダーマッパー
 */
class ReminderRemoteDataSource(
    private val supabaseClient: SupabaseClient,
    private val reminderMapper: ReminderMapper
) {
    companion object {
        const val TABLE_NAME = "reminders"
    }

    /**
     * リマインダーを作成する
     *
     * @param reminder リマインダー
     * @return 作成したリマインダー
     * @throws Exception リマインダーの作成に失敗した場合
     */
    suspend fun createReminder(reminder: Reminder): Result<Reminder> {
        try {
            val reminderDto = reminderMapper.mapToDto(reminder)
            supabaseClient.from(TABLE_NAME).insert(reminderDto)
            return Result.success(reminder)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    /**
     * リマインダーを取得する
     *
     * @return リマインダーのリスト
     * @throws Exception リマインダーの取得に失敗した場合
     */
    suspend fun getReminders(): Result<List<Reminder>> {
        try {
            val result = supabaseClient.from(TABLE_NAME).select().decodeList<ReminderDto>()
            return Result.success(result.map { reminderMapper.mapToEntity(it) })
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    /**
     * リマインダーを削除する
     *
     * @param reminderId 削除するリマインダーのID
     * @return 削除結果
     * @throws Exception リマインダーの削除に失敗した場合
     */
    suspend fun deleteReminder(reminderId: String): Result<Unit> {
        return try {
            supabaseClient.from(TABLE_NAME).delete {
                filter {
                    eq("id", reminderId)
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 期限切れリマインダーを削除する
     *
     * @param currentTimeMillis 現在時刻（エポックミリ秒）
     * @return 削除結果
     * @throws Exception リマインダーの削除に失敗した場合
     */
    suspend fun deleteExpiredReminders(currentTimeMillis: Long): Result<Unit> {
        return try {
            supabaseClient.from(TABLE_NAME).delete {
                filter {
                    lt("forget_at", currentTimeMillis)
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
