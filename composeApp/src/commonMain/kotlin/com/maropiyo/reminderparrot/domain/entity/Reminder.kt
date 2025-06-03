package com.maropiyo.reminderparrot.domain.entity

/**
 * リマインダー
 *
 * @param id リマインダーID
 * @param text リマインダーテキスト
 * @param isCompleted 完了フラグ
 */
data class Reminder(
    val id: String,
    val text: String,
    val isCompleted: Boolean = false
)
