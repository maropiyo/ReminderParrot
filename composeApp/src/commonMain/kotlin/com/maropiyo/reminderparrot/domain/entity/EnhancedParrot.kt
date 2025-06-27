package com.maropiyo.reminderparrot.domain.entity

/**
 * リマインコの統計情報
 *
 * @param memorizedWords 覚えられることばの数
 * @param memoryTimeHours 記憶時間（時間）
 * @param emotionalRange 感情表現の幅（1=基本、3=豊か、10=究極）
 * @param socialConnections 社会的つながりの数
 * @param learningSpeed 学習速度の倍率
 * @param streakDays 連続達成日数
 */
data class ParrotStats(
    val memorizedWords: Int = 1,
    val memoryTimeHours: Int = 1,
    val emotionalRange: Int = 1,
    val socialConnections: Int = 0,
    val learningSpeed: Float = 1.0f,
    val streakDays: Int = 0
)

/**
 * リマインコの個性特性
 *
 * @param cheerfulness 明るさ（0-100）
 * @param diligence 勤勉さ（0-100）
 * @param curiosity 好奇心（0-100）
 * @param empathy 共感性（0-100）
 */
data class ParrotPersonality(
    val cheerfulness: Int = 50,
    val diligence: Int = 50,
    val curiosity: Int = 50,
    val empathy: Int = 50
)

/**
 * スキルシステムを含む強化されたリマインコエンティティ
 *
 * @param level かしこさレベル
 * @param currentExperience 現在の経験値
 * @param maxExperience レベルアップに必要な経験値
 * @param unlockedSkills 解放済みスキルのIDセット
 * @param stats 統計情報
 * @param personality 個性特性
 * @param cosmeticItems 所持している装飾アイテムのIDセット
 * @param activeCosmeticItems 現在装備中の装飾アイテムのIDセット
 */
data class EnhancedParrot(
    val level: Int = 1,
    val currentExperience: Int = 0,
    val maxExperience: Int = 10,
    val unlockedSkills: Set<String> = emptySet(),
    val stats: ParrotStats = ParrotStats(),
    val personality: ParrotPersonality = ParrotPersonality(),
    val cosmeticItems: Set<String> = emptySet(),
    val activeCosmeticItems: Set<String> = emptySet()
) {

    /**
     * スキルが解放されているかチェック
     */
    fun hasSkill(skillId: String): Boolean = skillId in unlockedSkills

    /**
     * 特定のカテゴリのスキルを持っているかチェック
     */
    fun hasSkillInCategory(category: SkillCategory): Boolean {
        return ParrotSkills.getSkillsByCategory(category)
            .any { skill -> hasSkill(skill.id) }
    }

    /**
     * 解放可能なスキルを取得
     */
    fun getAvailableSkills(): List<ParrotSkill> {
        return ParrotSkills.ALL_SKILLS.filter { skill ->
            !hasSkill(skill.id) && // 未解放
                level >= skill.requiredLevel && // レベル要件を満たす
                skill.prerequisites.all { prereqId -> hasSkill(prereqId) } // 前提スキル要件を満たす
        }
    }

    /**
     * ロックされているスキルを取得
     */
    fun getLockedSkills(): List<ParrotSkill> {
        return ParrotSkills.ALL_SKILLS.filter { skill ->
            !hasSkill(skill.id) && // 未解放
                (
                    level < skill.requiredLevel || // レベル要件を満たさない
                        !skill.prerequisites.all { prereqId -> hasSkill(prereqId) }
                    ) // 前提スキル要件を満たさない
        }
    }

    /**
     * 解放済みスキルを取得
     */
    fun getUnlockedSkills(): List<ParrotSkill> {
        return unlockedSkills.mapNotNull { skillId ->
            ParrotSkills.getSkillById(skillId)
        }
    }

    /**
     * 次のレベルまでの進捗率を取得（0.0 - 1.0）
     */
    fun getExperienceProgress(): Float {
        return if (maxExperience > 0) {
            (currentExperience.toFloat() / maxExperience.toFloat()).coerceIn(0f, 1f)
        } else {
            0f
        }
    }

    /**
     * 個性に基づいたメッセージトーンを取得
     */
    fun getMessageTone(): MessageTone {
        return when {
            personality.cheerfulness >= 70 -> MessageTone.CHEERFUL
            personality.diligence >= 70 -> MessageTone.SERIOUS
            personality.curiosity >= 70 -> MessageTone.CURIOUS
            personality.empathy >= 70 -> MessageTone.CARING
            else -> MessageTone.BALANCED
        }
    }
}

/**
 * メッセージのトーン
 */
enum class MessageTone {
    CHEERFUL, // 明るい
    SERIOUS, // 真面目
    CURIOUS, // 好奇心旺盛
    CARING, // 思いやりのある
    BALANCED // バランス型
}
