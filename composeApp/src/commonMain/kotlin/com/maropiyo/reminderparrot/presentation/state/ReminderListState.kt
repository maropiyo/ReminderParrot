package com.maropiyo.reminderparrot.presentation.state

import com.maropiyo.reminderparrot.domain.entity.Reminder

/**
 * リマインダー一覧画面の状態
 *
 * @property reminders リマインダーのリスト
 * @property isLoading ローディング中かどうか
 * @property error エラーメッセージ
 * @property lastUpdated 最終更新時刻（リアルタイム表示用）
 */
data class ReminderListState(
    val reminders: List<Reminder> = listOf(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val lastUpdated: Long = 0L
)
