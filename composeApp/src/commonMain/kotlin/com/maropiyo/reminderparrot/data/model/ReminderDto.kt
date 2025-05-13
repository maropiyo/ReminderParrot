package com.maropiyo.reminderparrot.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * リマインダーDTO
 *
 * @property id リマインダーID
 * @property text リマインダーテキスト
 */
@Serializable
data class ReminderDto(
    @SerialName("id")
    val id: String? = null,
    @SerialName("text")
    val text: String,
)
