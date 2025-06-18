package com.maropiyo.reminderparrot.domain.entity

import kotlinx.datetime.Instant

/**
 * リマインダー
 *
 * @param id リマインダーID
 * @param text リマインダーテキスト
 * @param isCompleted 完了フラグ
 * @param createdAt 作成日時
 * @param forgetAt 忘却日時
 */
data class Reminder(
    val id: String,
    val text: String,
    val isCompleted: Boolean = false,
    val createdAt: Instant,
    val forgetAt: Instant
)
