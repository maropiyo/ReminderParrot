package com.maropiyo.reminderparrot.data.mapper

import com.maropiyo.reminderparrot.domain.entity.Parrot

/**
 * インコのマッパー
 */
class ParrotMapper {
    /**
     * SQLDelightのクエリ結果をエンティティに変換する
     *
     * @param id ID（使用しない）
     * @param level かしこさレベル
     * @param current_experience 現在の経験値
     * @param max_experience レベルアップに必要な経験値
     * @param memorized_words 覚えられることばの数
     * @param memory_time_hours 記憶時間(時間)
     * @return インコ
     */
    fun mapFromDatabase(
        id: Long,
        level: Long,
        current_experience: Long,
        max_experience: Long,
        memorized_words: Long,
        memory_time_hours: Long
    ): Parrot = Parrot(
        level = level.toInt(),
        currentExperience = current_experience.toInt(),
        maxExperience = max_experience.toInt(),
        memorizedWords = memorized_words.toInt(),
        memoryTimeHours = memory_time_hours.toInt()
    )
}
