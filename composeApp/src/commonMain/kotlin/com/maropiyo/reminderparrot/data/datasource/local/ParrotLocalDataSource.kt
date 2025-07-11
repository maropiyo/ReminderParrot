package com.maropiyo.reminderparrot.data.datasource.local

import com.maropiyo.reminderparrot.data.mapper.ParrotMapper
import com.maropiyo.reminderparrot.db.ReminderParrotDatabase
import com.maropiyo.reminderparrot.domain.entity.Parrot

/**
 * インコのローカルデータソース
 *
 * @property database データベース
 * @property parrotMapper インコマッパー
 */
class ParrotLocalDataSource(
    private val database: ReminderParrotDatabase,
    private val parrotMapper: ParrotMapper
) {
    /**
     * インコのデータを取得する
     *
     * @return インコのデータ（存在しない場合はnull）
     */
    fun getParrot(): Parrot? =
        database.reminderParrotDatabaseQueries.selectParrot(parrotMapper::mapFromDatabase).executeAsOneOrNull()

    /**
     * インコのデータを保存する
     *
     * @param parrot インコのデータ
     */
    fun saveParrot(parrot: Parrot) {
        database.reminderParrotDatabaseQueries.insertParrot(
            level = parrot.level.toLong(),
            current_experience = parrot.currentExperience.toLong(),
            max_experience = parrot.maxExperience.toLong(),
            memorized_words = parrot.memorizedWords.toLong(),
            memory_time_hours = parrot.memoryTimeHours.toLong()
        )
    }

    /**
     * インコのデータを更新する
     *
     * @param parrot インコのデータ
     */
    fun updateParrot(parrot: Parrot) {
        database.reminderParrotDatabaseQueries.updateParrot(
            level = parrot.level.toLong(),
            current_experience = parrot.currentExperience.toLong(),
            max_experience = parrot.maxExperience.toLong(),
            memorized_words = parrot.memorizedWords.toLong(),
            memory_time_hours = parrot.memoryTimeHours.toLong()
        )
    }
}
