package com.maropiyo.reminderparrot.domain.entity

/**
 * リマインダー
 *
 * @param id リマインダーID
 * @param text リマインダーテキスト
 */
data class Reminder(
    val id: String,
    val text: String
)
