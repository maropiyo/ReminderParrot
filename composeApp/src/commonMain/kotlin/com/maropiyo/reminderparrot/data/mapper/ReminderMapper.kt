package com.maropiyo.reminderparrot.data.mapper

import com.maropiyo.reminderparrot.data.model.ReminderDto
import com.maropiyo.reminderparrot.domain.entity.Reminder
import kotlinx.datetime.Instant

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
    fun mapToEntity(dto: ReminderDto): Reminder = Reminder(
        id = dto.id,
        text = dto.text,
        isCompleted = dto.isCompleted,
        createdAt = Instant.fromEpochMilliseconds(dto.createdAt),
        forgetAt = Instant.fromEpochMilliseconds(dto.forgetAt)
    )

    /**
     * エンティティをDTOに変換する
     *
     * @param entity リマインダーエンティティ
     * @return リマインダーDTO
     */
    fun mapToDto(entity: Reminder): ReminderDto = ReminderDto(
        id = entity.id,
        text = entity.text,
        isCompleted = entity.isCompleted,
        createdAt = entity.createdAt.toEpochMilliseconds(),
        forgetAt = entity.forgetAt.toEpochMilliseconds()
    )

    /**
     * SQLDelightのクエリ結果をエンティティに変換する
     *
     * @param id リマインダーID
     * @param text リマインダーテキスト
     * @param isCompleted 完了フラグ
     * @param createdAt 作成日時（エポックミリ秒）
     * @param forgetAt 忘却日時（エポックミリ秒）
     * @return リマインダー
     */
    fun mapFromDatabase(id: String, text: String, isCompleted: Long, createdAt: Long, forgetAt: Long): Reminder =
        Reminder(
            id = id,
            text = text,
            isCompleted = isCompleted == 1L,
            createdAt = Instant.fromEpochMilliseconds(createdAt),
            forgetAt = Instant.fromEpochMilliseconds(forgetAt)
        )
}
