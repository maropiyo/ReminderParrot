package com.maropiyo.reminderparrot.domain.entity

/**
 * インコのエンティティ
 *
 * @param level かしこさレベル
 * @param currentExperience 現在の経験値
 * @param maxExperience レベルアップに必要な経験値
 * @param memorizedWords 覚えられることばの数
 * @param memoryTimeHours 記憶時間(時間)
 */
data class Parrot(
    val level: Int = 1,
    val currentExperience: Int = 0,
    val maxExperience: Int = 1,
    val memorizedWords: Int = 1,
    val memoryTimeHours: Int = 1
)
