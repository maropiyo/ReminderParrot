package com.maropiyo.reminderparrot.presentation.state

import com.maropiyo.reminderparrot.domain.entity.Reminder

/**
 * ホーム画面の状態
 *
 * @property reminders リマインダーのリスト
 * @property isLoading ローディング中かどうか
 * @property error エラーメッセージ
 */
data class HomeState(
    val reminders: List<Reminder> = listOf(),
    val isLoading: Boolean = false,
    val error: String? = null
)
