package com.maropiyo.reminderparrot.presentation.state

import com.maropiyo.reminderparrot.domain.entity.Reminder

/**
 * リマインダー一覧画面の状態
 *
 * @property reminders リマインダーのリスト
 * @property isLoading ローディング中かどうか
 * @property error エラーメッセージ
 */
data class ReminderListState(
    val reminders: List<Reminder> = listOf(),
    val isLoading: Boolean = false,
    val error: String? = null
)
