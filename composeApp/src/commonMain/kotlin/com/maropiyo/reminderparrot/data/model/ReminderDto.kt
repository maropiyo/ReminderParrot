package com.maropiyo.reminderparrot.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * リマインダーDTO
 *
 * @property id リマインダーID
 * @property text リマインダーテキスト
 * @property isCompleted 完了フラグ
 * @property createdAt 作成日時（エポックミリ秒）
 * @property forgetAt 忘却日時（エポックミリ秒）
 */
@Serializable
data class ReminderDto(
    @SerialName("id")
    val id: String,
    @SerialName("text")
    val text: String,
    @SerialName("is_completed")
    val isCompleted: Boolean = false,
    @SerialName("created_at")
    val createdAt: Long,
    @SerialName("forget_at")
    val forgetAt: Long
)
