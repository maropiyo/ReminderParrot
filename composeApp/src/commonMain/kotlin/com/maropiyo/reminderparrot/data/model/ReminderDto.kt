package com.maropiyo.reminderparrot.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * リマインダーDTO
 *
 * @property id リマインダーID
 * @property text リマインダーテキスト
 * @property isCompleted 完了フラグ
 */
@Serializable
data class ReminderDto(
    @SerialName("id")
    val id: String,
    @SerialName("text")
    val text: String,
    @SerialName("is_completed")
    val isCompleted: Boolean = false
)
