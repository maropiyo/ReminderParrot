package com.maropiyo.reminderparrot.domain.usecase

import com.maropiyo.reminderparrot.domain.entity.EnhancedParrot
import com.maropiyo.reminderparrot.domain.entity.ParrotSkills
import com.maropiyo.reminderparrot.domain.entity.ParrotStats
import kotlin.math.max
import kotlin.math.pow

/**
 * スキルベースの統計計算UseCase
 *
 * インコのレベルと解放されたスキルに基づいて、
 * 能力値を動的に計算する
 */
class CalculateStatsUseCase {

    /**
     * インコの統計情報を計算
     *
     * @param parrot インコの状態
     * @return 計算された統計情報
     */
    fun execute(parrot: EnhancedParrot): ParrotStats {
        var stats = calculateBaseStats(parrot.level)

        // スキルによる能力ボーナスを適用
        parrot.unlockedSkills.forEach { skillId ->
            stats = applySkillBonus(stats, skillId, parrot.level)
        }

        return stats
    }

    /**
     * レベルベースの基本統計を計算
     *
     * @param level インコのレベル
     * @return 基本統計情報
     */
    private fun calculateBaseStats(level: Int): ParrotStats {
        return ParrotStats(
            memorizedWords = calculateMemorizedWords(level),
            memoryTimeHours = calculateMemoryTimeHours(level),
            emotionalRange = 1, // 基本は1、スキルで拡張
            socialConnections = 0, // スキルで増加
            learningSpeed = 1.0f, // 基本は1.0、スキルで向上
            streakDays = 0 // リアルタイムで更新
        )
    }

    /**
     * レベルに応じた記憶語数の計算
     * 指数関数的に成長するが、上限を設ける
     */
    private fun calculateMemorizedWords(level: Int): Int {
        val base = 2.0
        val multiplier = 0.8
        val result = (base.pow(level * multiplier)).toInt()
        return max(1, result).coerceAtMost(10000) // 1〜10000の範囲
    }

    /**
     * レベルに応じた記憶時間の計算
     * より現実的な成長カーブ
     */
    private fun calculateMemoryTimeHours(level: Int): Int {
        return when {
            level <= 5 -> level // 1-5時間
            level <= 15 -> 5 + (level - 5) * 2 // 6-25時間
            level <= 30 -> 25 + (level - 15) * 3 // 26-70時間
            else -> 70 + (level - 30) * 5 // 71時間以上
        }.coerceAtMost(8760) // 最大1年間
    }

    /**
     * スキルによる能力ボーナスを適用
     *
     * @param currentStats 現在の統計
     * @param skillId スキルID
     * @param level インコのレベル
     * @return ボーナス適用後の統計
     */
    private fun applySkillBonus(currentStats: ParrotStats, skillId: String, level: Int): ParrotStats {
        return when (skillId) {
            ParrotSkills.BASIC_EMOTIONS.id -> currentStats.copy(
                emotionalRange = 3 // 基本感情：3種類の感情表現
            )

            ParrotSkills.RICH_EMOTIONS.id -> currentStats.copy(
                emotionalRange = 10 // 豊かな感情：10種類の感情表現
            )

            ParrotSkills.PATTERN_LEARNING.id -> currentStats.copy(
                learningSpeed = currentStats.learningSpeed * 1.5f, // 学習速度1.5倍
                memorizedWords = (currentStats.memorizedWords * 1.2).toInt() // 記憶語数20%増
            )

            ParrotSkills.FAMILY_BONDING.id -> currentStats.copy(
                socialConnections = currentStats.socialConnections + 5 // 家族5人分の接続
            )

            ParrotSkills.EMOTIONAL_SUPPORT.id -> currentStats.copy(
                socialConnections = currentStats.socialConnections + 3, // 追加の社会的つながり
                emotionalRange = maxOf(currentStats.emotionalRange, 7) // 最低でも7種類の感情
            )

            ParrotSkills.STREAK_TRACKING.id -> currentStats.copy(
                // ストリーク追跡は別途リアルタイム計算
                learningSpeed = currentStats.learningSpeed * 1.1f // 継続による学習効率向上
            )

            ParrotSkills.WEATHER_AWARENESS.id -> currentStats.copy(
                learningSpeed = currentStats.learningSpeed * 1.2f, // 環境認識による学習向上
                memorizedWords = (currentStats.memorizedWords * 1.1).toInt() // 天気関連語彙追加
            )

            ParrotSkills.PHOTO_MEMORIES.id -> currentStats.copy(
                memoryTimeHours = (currentStats.memoryTimeHours * 1.3).toInt() // 写真による記憶強化
            )

            ParrotSkills.BEHAVIORAL_ANALYSIS.id -> currentStats.copy(
                learningSpeed = currentStats.learningSpeed * 2.0f, // 高度な分析能力
                memorizedWords = (currentStats.memorizedWords * 1.5).toInt()
            )

            ParrotSkills.MEMORY_GARDEN.id -> currentStats.copy(
                memoryTimeHours = (currentStats.memoryTimeHours * 1.5).toInt(), // 庭での記憶保存
                emotionalRange = maxOf(currentStats.emotionalRange, 5) // 美的感覚
            )

            ParrotSkills.SEASONAL_MODES.id -> currentStats.copy(
                emotionalRange = maxOf(currentStats.emotionalRange, 6), // 季節感情
                learningSpeed = currentStats.learningSpeed * 1.15f
            )

            ParrotSkills.DREAM_DIARY.id -> currentStats.copy(
                memoryTimeHours = (currentStats.memoryTimeHours * 1.8).toInt(), // 睡眠記憶
                learningSpeed = currentStats.learningSpeed * 1.1f
            )

            ParrotSkills.MUSICAL_ABILITY.id -> currentStats.copy(
                emotionalRange = maxOf(currentStats.emotionalRange, 8), // 音楽的感情表現
                memorizedWords = (currentStats.memorizedWords * 1.2).toInt() // 音楽語彙
            )

            ParrotSkills.MASTER_COMPANION.id -> currentStats.copy(
                // 究極の相棒：全能力大幅向上
                memorizedWords = (currentStats.memorizedWords * 2.0).toInt(),
                memoryTimeHours = (currentStats.memoryTimeHours * 2.0).toInt(),
                emotionalRange = 15, // 究極の感情表現
                socialConnections = currentStats.socialConnections + 10,
                learningSpeed = currentStats.learningSpeed * 3.0f
            )

            else -> currentStats // 未定義スキルの場合は変更なし
        }
    }

    /**
     * レベルとスキルに基づく最大経験値を計算
     *
     * @param level 対象レベル
     * @param hasLearningBonus 学習ボーナスがあるか
     * @return 必要経験値
     */
    fun calculateMaxExperience(level: Int, hasLearningBonus: Boolean = false): Int {
        val base = when {
            level <= 10 -> level * 10 // 1-10レベル：10, 20, 30...100
            level <= 25 -> 100 + (level - 10) * 20 // 11-25レベル：120, 140...400
            level <= 40 -> 400 + (level - 25) * 30 // 26-40レベル：430, 460...850
            else -> 850 + (level - 40) * 50 // 41レベル以上：900, 950...
        }

        // 学習ボーナスがある場合は必要経験値を少し減らす
        return if (hasLearningBonus) {
            (base * 0.9).toInt()
        } else {
            base
        }
    }
}
