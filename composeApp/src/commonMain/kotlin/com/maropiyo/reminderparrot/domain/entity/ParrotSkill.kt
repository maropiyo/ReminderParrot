package com.maropiyo.reminderparrot.domain.entity

/**
 * リマインコのスキルカテゴリ
 */
enum class SkillCategory {
    EXPRESSION, // 表現系：感情、音声、見た目
    INTELLIGENCE, // 知的系：学習、予測、分析
    SOCIAL, // 社会性系：家族連携、共有機能
    SPECIAL // 特殊系：記憶の庭、季節モード等
}

/**
 * リマインコのスキル定義
 *
 * @param id スキルID
 * @param name スキル名
 * @param description スキルの説明
 * @param requiredLevel 解放に必要なレベル
 * @param category スキルカテゴリ
 * @param prerequisites 前提スキルのIDリスト
 */
data class ParrotSkill(
    val id: String,
    val name: String,
    val description: String,
    val requiredLevel: Int,
    val category: SkillCategory,
    val prerequisites: List<String> = emptyList()
)

/**
 * 定義済みスキル一覧
 */
object ParrotSkills {

    // 表現系スキル
    val BASIC_EMOTIONS = ParrotSkill(
        id = "basic_emotions",
        name = "基本感情",
        description = "喜怒哀楽の表情を表現できるようになります",
        requiredLevel = 2,
        category = SkillCategory.EXPRESSION
    )

    val VOICE_GREETING = ParrotSkill(
        id = "voice_greeting", name = "音声挨拶",
        description = "時間帯に応じた挨拶をするようになります",
        requiredLevel = 3,
        category = SkillCategory.EXPRESSION,
        prerequisites = listOf("basic_emotions")
    )

    val COLOR_PERCEPTION = ParrotSkill(
        id = "color_perception",
        name = "色彩認識", description = "リマインダーをカテゴリ別に色分けできます",
        requiredLevel = 4,
        category = SkillCategory.EXPRESSION
    )

    val CUSTOM_NOTIFICATIONS = ParrotSkill(
        id = "custom_notifications",
        name = "通知カスタマイズ",
        description = "通知音とメッセージをカスタマイズできます",
        requiredLevel = 5,
        category = SkillCategory.EXPRESSION,
        prerequisites = listOf("voice_greeting")
    )

    val RICH_EMOTIONS = ParrotSkill(
        id = "rich_emotions",
        name = "豊かな感情",
        description = "10種類の感情表現ができるようになります",
        requiredLevel = 35,
        category = SkillCategory.EXPRESSION,
        prerequisites = listOf("basic_emotions")
    )

    // 知的系スキル
    val STREAK_TRACKING = ParrotSkill(
        id = "streak_tracking",
        name = "連続記録",
        description = "タスクの連続達成日数を記録します",
        requiredLevel = 7,
        category = SkillCategory.INTELLIGENCE
    )

    val PATTERN_LEARNING = ParrotSkill(
        id = "pattern_learning",
        name = "パターン学習",
        description = "あなたの行動パターンを学習して提案します",
        requiredLevel = 10,
        category = SkillCategory.INTELLIGENCE
    )

    val PHOTO_MEMORIES = ParrotSkill(
        id = "photo_memories",
        name = "写真記録",
        description = "リマインダーに写真を添付できます",
        requiredLevel = 12,
        category = SkillCategory.INTELLIGENCE,
        prerequisites = listOf("pattern_learning")
    )

    val WEATHER_AWARENESS = ParrotSkill(
        id = "weather_awareness",
        name = "天気認識",
        description = "天気に応じた提案をするようになります",
        requiredLevel = 15,
        category = SkillCategory.INTELLIGENCE,
        prerequisites = listOf("pattern_learning")
    )

    val BEHAVIORAL_ANALYSIS = ParrotSkill(
        id = "behavioral_analysis",
        name = "行動分析",
        description = "詳細な行動分析レポートを提供します",
        requiredLevel = 45,
        category = SkillCategory.INTELLIGENCE,
        prerequisites = listOf("weather_awareness")
    )

    // 社会性系スキル
    val FAMILY_BONDING = ParrotSkill(
        id = "family_bonding",
        name = "家族絆",
        description = "家族間でリマインダーを共有できます",
        requiredLevel = 18,
        category = SkillCategory.SOCIAL
    )

    val EMOTIONAL_SUPPORT = ParrotSkill(
        id = "emotional_support",
        name = "感情サポート",
        description = "気持ちに寄り添った励ましをします",
        requiredLevel = 30,
        category = SkillCategory.SOCIAL,
        prerequisites = listOf("family_bonding", "rich_emotions")
    )

    // 特殊系スキル
    val MEMORY_GARDEN = ParrotSkill(
        id = "memory_garden",
        name = "記憶の庭",
        description = "完了したタスクを美しい花に変えて記録します",
        requiredLevel = 20,
        category = SkillCategory.SPECIAL
    )

    val SEASONAL_MODES = ParrotSkill(
        id = "seasonal_modes",
        name = "季節モード",
        description = "季節に応じて見た目が変化します",
        requiredLevel = 25,
        category = SkillCategory.SPECIAL,
        prerequisites = listOf("weather_awareness")
    )

    val DREAM_DIARY = ParrotSkill(
        id = "dream_diary",
        name = "夢日記",
        description = "睡眠と夢の記録機能を使えます",
        requiredLevel = 30,
        category = SkillCategory.SPECIAL,
        prerequisites = listOf("memory_garden")
    )

    val MUSICAL_ABILITY = ParrotSkill(
        id = "musical_ability",
        name = "音楽的才能",
        description = "BGMや効果音機能が使えます",
        requiredLevel = 40,
        category = SkillCategory.SPECIAL,
        prerequisites = listOf("custom_notifications")
    )

    val MASTER_COMPANION = ParrotSkill(
        id = "master_companion",
        name = "マスターコンパニオン",
        description = "究極の相棒として特別なアニメーションが解放されます",
        requiredLevel = 50,
        category = SkillCategory.SPECIAL,
        prerequisites = listOf("behavioral_analysis", "emotional_support", "musical_ability")
    )

    /**
     * 全スキルのリスト
     */
    val ALL_SKILLS = listOf(
        BASIC_EMOTIONS, VOICE_GREETING, COLOR_PERCEPTION, CUSTOM_NOTIFICATIONS, RICH_EMOTIONS,
        STREAK_TRACKING, PATTERN_LEARNING, PHOTO_MEMORIES, WEATHER_AWARENESS, BEHAVIORAL_ANALYSIS,
        FAMILY_BONDING, EMOTIONAL_SUPPORT,
        MEMORY_GARDEN, SEASONAL_MODES, DREAM_DIARY, MUSICAL_ABILITY, MASTER_COMPANION
    )

    /**
     * スキルIDからスキルを取得
     */
    fun getSkillById(id: String): ParrotSkill? = ALL_SKILLS.find { it.id == id }

    /**
     * カテゴリ別のスキルを取得
     */
    fun getSkillsByCategory(category: SkillCategory): List<ParrotSkill> = ALL_SKILLS.filter { it.category == category }

    /**
     * レベル別の解放スキルを取得
     */
    fun getSkillsForLevel(level: Int): List<ParrotSkill> = ALL_SKILLS.filter { it.requiredLevel == level }
}
