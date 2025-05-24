package com.maropiyo.reminderparrot.data.mapper

import com.maropiyo.reminderparrot.data.model.ReminderDto
import com.maropiyo.reminderparrot.domain.entity.Reminder

/**
 * リマインダーのマッパー
 */
class ReminderMapper {
    /**
     * DTOをエンティティに変換する
     *
     * @param dto リマインダーDTO
     * @return リマインダー
     */
    fun mapToEntity(dto: ReminderDto): Reminder =
        Reminder(
            id = dto.id,
            text = dto.text
        )

    /**
     * エンティティをDTOに変換する
     *
     * @param entity リマインダーエンティティ
     * @return リマインダーDTO
     */
    fun mapToDto(entity: Reminder): ReminderDto =
        ReminderDto(
            id = entity.id,
            text = entity.text
        )

    /**
     * SQLDelightのクエリ結果をエンティティに変換する
     *
     * @param id リマインダーID
     * @param text リマインダーテキスト
     * @return リマインダー
     */
    fun mapFromDatabase(
        id: String,
        text: String
    ): Reminder =
        Reminder(
            id = id,
            text = text
        )
}
